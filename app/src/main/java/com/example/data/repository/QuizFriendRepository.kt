package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.firebase.OperationType
import com.example.data.firebase.handleFirestoreError
import com.example.data.model.FriendChallenge
import com.example.data.model.Friendship
import com.example.data.model.GameRoom
import com.example.data.model.LeaderboardEntry
import com.example.data.model.MatchHistory
import com.example.data.model.MissionProgress
import com.example.data.model.ModerationReport
import com.example.data.model.ReviewItem
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class QuizFriendRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String =
        Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("Utilisateur non connecté.")

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val docRef = db.collection("users").document(userId)
        docRef.snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else null
            }
            .catch { error ->
                handleFlowError(error, OperationType.GET, docRef.path, userId)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun ensureUserProfile(userId: String, displayName: String?, email: String?): UserProfile {
        val docRef = db.collection("users").document(userId)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                val existing = snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                if (existing != null) {
                    if (existing.lastLoginDate != today) {
                        val nextStreak = existing.streakDays + 1L
                        val bonusCoins = (25L + (nextStreak.coerceAtMost(7L) * 10L))
                        docRef.update(
                            mapOf(
                                "lastLoginDate" to today,
                                "streakDays" to nextStreak,
                                "quizCoins" to existing.quizCoins + bonusCoins,
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        ).await()
                    }
                    ensureInitialMissions(userId)
                    return existing
                }
            }
            val cleanPseudo = (displayName?.takeIf { it.isNotBlank() } ?: "Joueur_${userId.take(5)}").take(24)
            val payload = mapOf(
                "userId" to userId,
                "pseudo" to cleanPseudo,
                "avatarId" to "avatar_classic_spark",
                "frameId" to "frame_none",
                "themeId" to "dark_gaming",
                "titleId" to "title_rookie",
                "level" to 1L,
                "xp" to 0L,
                "quizCoins" to 300L,
                "wins" to 0L,
                "gamesPlayed" to 0L,
                "correctAnswers" to 0L,
                "totalAnswers" to 0L,
                "bestScore" to 0L,
                "streakDays" to 1L,
                "lastLoginDate" to today,
                "division" to "Bronze",
                "country" to "SN",
                "isPublic" to true,
                "showStats" to true,
                "allowInvites" to true,
                "reactionsEnabled" to true,
                "isMinorSafe" to false,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()

            if (!email.isNullOrBlank()) {
                val privateRef = docRef.collection("private").document("info")
                privateRef.set(
                    mapOf(
                        "userId" to userId,
                        "email" to email,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            syncLeaderboardEntry(
                userId = userId,
                pseudo = cleanPseudo,
                avatarId = "avatar_classic_spark",
                division = "Bronze",
                country = "SN",
                seasonPoints = 0L,
                wins = 0L
            )
            ensureInitialMissions(userId)
            return docRef.get().await().toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                ?: UserProfile(userId = userId, pseudo = cleanPseudo)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    private suspend fun ensureInitialMissions(userId: String) {
        val missionsRef = db.collection("users").document(userId).collection("missions")
        val existing = missionsRef.get().await()
        if (!existing.isEmpty) return
        val defaults = listOf(
            Triple("win_3", "daily", 3L),
            Triple("answer_20", "daily", 20L),
            Triple("categories_3", "daily", 3L),
            Triple("streak_5", "weekly", 5L),
            Triple("challenge_friend", "weekly", 1L),
            Triple("play_tournament", "weekly", 1L)
        )
        for ((key, period, target) in defaults) {
            missionsRef.document(key).set(
                mapOf(
                    "userId" to userId,
                    "missionKey" to key,
                    "period" to period,
                    "progress" to 0L,
                    "target" to target,
                    "claimed" to false,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun updateProfileFields(userId: String, fields: Map<String, Any>) {
        val docRef = db.collection("users").document(userId)
        val payload = fields.toMutableMap().apply {
            this["updatedAt"] = FieldValue.serverTimestamp()
        }
        try {
            docRef.update(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun syncLeaderboardEntry(
        userId: String,
        pseudo: String,
        avatarId: String,
        division: String,
        country: String,
        seasonPoints: Long,
        wins: Long
    ) {
        val ref = db.collection("leaderboard").document(userId)
        try {
            val snap = ref.get().await()
            val payload = mutableMapOf<String, Any>(
                "userId" to userId,
                "pseudo" to pseudo,
                "avatarId" to avatarId,
                "division" to division,
                "country" to country,
                "seasonPoints" to seasonPoints,
                "weeklyPoints" to seasonPoints,
                "monthlyPoints" to seasonPoints,
                "wins" to wins,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!snap.exists()) {
                payload["createdAt"] = FieldValue.serverTimestamp()
                ref.set(payload).await()
            } else {
                ref.update(payload).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, ref.path)
        }
    }

    fun observeLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        val query = db.collection("leaderboard")
            .orderBy("seasonPoints", Query.Direction.DESCENDING)
            .limit(100)
        query.snapshots()
            .map { it.toObjects(LeaderboardEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { error ->
                handleFlowError(error, OperationType.LIST, "leaderboard", Firebase.auth.currentUser?.uid ?: "")
                throw error
            }
            .collect { emit(it) }
    }

    fun observeMatchHistory(userId: String): Flow<List<MatchHistory>> = flow {
        val col = db.collection("users").document(userId).collection("history")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(30)
        col.snapshots()
            .map { it.toObjects(MatchHistory::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { error ->
                handleFlowError(error, OperationType.LIST, "users/$userId/history", userId)
                throw error
            }
            .collect { emit(it) }
    }

    fun observeReviewItems(userId: String): Flow<List<ReviewItem>> = flow {
        val col = db.collection("users").document(userId).collection("review_items")
        col.snapshots()
            .map { it.toObjects(ReviewItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { error ->
                handleFlowError(error, OperationType.LIST, "users/$userId/review_items", userId)
                throw error
            }
            .collect { emit(it) }
    }

    fun observeMissions(userId: String): Flow<List<MissionProgress>> = flow {
        val col = db.collection("users").document(userId).collection("missions")
        col.snapshots()
            .map { it.toObjects(MissionProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { error ->
                handleFlowError(error, OperationType.LIST, "users/$userId/missions", userId)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun recordMatchResult(
        userId: String,
        profile: UserProfile,
        mode: String,
        category: String,
        score: Long,
        rank: Long,
        correctCount: Long,
        totalCount: Long,
        isRanked: Boolean,
        missedQuestionIds: List<Pair<String, String>>,
        xpEarned: Long,
        coinsEarned: Long
    ) {
        val historyRef = db.collection("users").document(userId).collection("history")
            .document("match_${UUID.randomUUID().toString().take(12)}")
        try {
            historyRef.set(
                mapOf(
                    "userId" to userId,
                    "mode" to mode,
                    "category" to category,
                    "score" to score,
                    "rank" to rank,
                    "correctCount" to correctCount,
                    "totalCount" to totalCount,
                    "xpEarned" to xpEarned,
                    "coinsEarned" to coinsEarned,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()

            val newXp = profile.xp + xpEarned
            val newLevel = (newXp / 250L) + 1L
            val newWins = profile.wins + if (rank == 1L && isRanked) 1L else 0L
            val newBest = maxOf(profile.bestScore, score)
            val seasonScore = (newXp / 2L) + (newWins * 120L)
            val newDivision = if (isRanked) {
                when {
                    seasonScore >= 10500L -> "Légende"
                    seasonScore >= 7500L -> "Champion"
                    seasonScore >= 5200L -> "Élite"
                    seasonScore >= 3500L -> "Diamant"
                    seasonScore >= 2200L -> "Platine"
                    seasonScore >= 1200L -> "Or"
                    seasonScore >= 500L -> "Argent"
                    else -> "Bronze"
                }
            } else profile.division

            updateProfileFields(
                userId,
                mapOf(
                    "xp" to newXp,
                    "level" to newLevel,
                    "quizCoins" to profile.quizCoins + coinsEarned,
                    "wins" to newWins,
                    "gamesPlayed" to profile.gamesPlayed + 1L,
                    "correctAnswers" to profile.correctAnswers + correctCount,
                    "totalAnswers" to profile.totalAnswers + totalCount,
                    "bestScore" to newBest,
                    "division" to newDivision
                )
            )

            if (isRanked) {
                syncLeaderboardEntry(
                    userId = userId,
                    pseudo = profile.pseudo,
                    avatarId = profile.avatarId,
                    division = newDivision,
                    country = profile.country,
                    seasonPoints = seasonScore,
                    wins = newWins
                )
            }

            for ((qId, qCat) in missedQuestionIds) {
                val revRef = db.collection("users").document(userId).collection("review_items").document(qId)
                val snap = revRef.get().await()
                if (snap.exists()) {
                    revRef.update(
                        mapOf(
                            "missedCount" to FieldValue.increment(1L),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                } else {
                    revRef.set(
                        mapOf(
                            "userId" to userId,
                            "questionId" to qId,
                            "category" to qCat,
                            "missedCount" to 1L,
                            "isFavorite" to false,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }

            // Update mission progress
            val missionsCol = db.collection("users").document(userId).collection("missions")
            if (rank == 1L) {
                missionsCol.document("win_3").set(
                    mapOf("progress" to FieldValue.increment(1L), "updatedAt" to FieldValue.serverTimestamp()),
                    SetOptions.merge()
                ).await()
            }
            if (correctCount > 0L) {
                missionsCol.document("answer_20").set(
                    mapOf("progress" to FieldValue.increment(correctCount), "updatedAt" to FieldValue.serverTimestamp()),
                    SetOptions.merge()
                ).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, historyRef.path)
            throw e
        }
    }

    suspend fun toggleFavoriteQuestion(userId: String, questionId: String, category: String, isFav: Boolean) {
        val revRef = db.collection("users").document(userId).collection("review_items").document(questionId)
        try {
            val snap = revRef.get().await()
            if (snap.exists()) {
                revRef.update(
                    mapOf(
                        "isFavorite" to isFav,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                revRef.set(
                    mapOf(
                        "userId" to userId,
                        "questionId" to questionId,
                        "category" to category,
                        "missedCount" to 0L,
                        "isFavorite" to isFav,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, revRef.path)
        }
    }

    suspend fun claimMission(userId: String, missionKey: String, profile: UserProfile, rewardXp: Long, rewardCoins: Long) {
        val mRef = db.collection("users").document(userId).collection("missions").document(missionKey)
        try {
            mRef.update(
                mapOf(
                    "claimed" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            val nextXp = profile.xp + rewardXp
            updateProfileFields(
                userId,
                mapOf(
                    "xp" to nextXp,
                    "level" to (nextXp / 250L) + 1L,
                    "quizCoins" to profile.quizCoins + rewardCoins
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, mRef.path)
            throw e
        }
    }

    // --- MULTIPLAYER ROOMS ---
    suspend fun createRoom(
        hostProfile: UserProfile,
        mode: String,
        category: String,
        difficulty: String,
        questionCount: Long,
        timePerQuestionSec: Long,
        maxPlayers: Long,
        questionIds: List<String>
    ): String {
        val roomId = "room_${UUID.randomUUID().toString().take(10)}"
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val code = (1..6).map { chars.random() }.joinToString("")
        val participantMap = mapOf(
            hostProfile.userId to mapOf(
                "pseudo" to hostProfile.pseudo,
                "avatarId" to hostProfile.avatarId,
                "ready" to true,
                "score" to 0L,
                "isBot" to false
            )
        )
        val docRef = db.collection("rooms").document(roomId)
        try {
            docRef.set(
                mapOf(
                    "roomCode" to code,
                    "hostId" to hostProfile.userId,
                    "mode" to mode,
                    "category" to category,
                    "difficulty" to difficulty,
                    "questionCount" to questionCount,
                    "timePerQuestionSec" to timePerQuestionSec,
                    "status" to "waiting",
                    "maxPlayers" to maxPlayers,
                    "participantIds" to listOf(hostProfile.userId),
                    "participants" to participantMap,
                    "currentQuestionIndex" to 0L,
                    "questionIds" to questionIds,
                    "reactions" to emptyList<Map<String, Any>>(),
                    "messages" to emptyList<Map<String, Any>>(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            return roomId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun joinRoomByCode(roomCode: String, profile: UserProfile): String? {
        try {
            val snap = db.collection("rooms")
                .whereEqualTo("status", "waiting")
                .whereEqualTo("roomCode", roomCode.uppercase())
                .limit(1)
                .get()
                .await()
            val doc = snap.documents.firstOrNull() ?: return null
            val room = doc.toObject(GameRoom::class.java) ?: return null
            if (room.participantIds.contains(profile.userId)) return doc.id
            if (room.participantIds.size >= room.maxPlayers) return null
            val nextIds = room.participantIds + profile.userId
            val nextParticipants = room.participants.toMutableMap().apply {
                this[profile.userId] = mapOf(
                    "pseudo" to profile.pseudo,
                    "avatarId" to profile.avatarId,
                    "ready" to false,
                    "score" to 0L,
                    "isBot" to false
                )
            }
            doc.reference.update(
                mapOf(
                    "participantIds" to nextIds,
                    "participants" to nextParticipants,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            return doc.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "rooms")
            throw e
        }
    }

    fun observeOpenRooms(): Flow<List<GameRoom>> = flow {
        db.collection("rooms")
            .whereEqualTo("status", "waiting")
            .limit(20)
            .snapshots()
            .map { it.toObjects(GameRoom::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { error ->
                handleFlowError(error, OperationType.LIST, "rooms", Firebase.auth.currentUser?.uid ?: "")
                throw error
            }
            .collect { emit(it) }
    }

    fun observeRoom(roomId: String): Flow<GameRoom?> = flow {
        val docRef = db.collection("rooms").document(roomId)
        docRef.snapshots()
            .map { if (it.exists()) it.toObject(GameRoom::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) else null }
            .catch { error ->
                handleFlowError(error, OperationType.GET, docRef.path, Firebase.auth.currentUser?.uid ?: "")
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun updateRoomFields(roomId: String, fields: Map<String, Any>) {
        val docRef = db.collection("rooms").document(roomId)
        try {
            val payload = fields.toMutableMap().apply {
                this["updatedAt"] = FieldValue.serverTimestamp()
            }
            docRef.update(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- FRIENDS, CHALLENGES, REPORTS ---
    fun observeFriendships(userId: String): Flow<List<Friendship>> = flow {
        db.collection("friendships")
            .whereArrayContains("userIds", userId)
            .snapshots()
            .map { it.toObjects(Friendship::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { error ->
                handleFlowError(error, OperationType.LIST, "friendships", userId)
                throw error
            }
            .collect { emit(it) }
    }

    fun observePublicPlayers(): Flow<List<UserProfile>> = flow {
        db.collection("users")
            .whereEqualTo("isPublic", true)
            .limit(40)
            .snapshots()
            .map { it.toObjects(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { error ->
                handleFlowError(error, OperationType.LIST, "users", Firebase.auth.currentUser?.uid ?: "")
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun sendFriendRequest(senderId: String, receiverId: String) {
        val id = listOf(senderId, receiverId).sorted().joinToString("_")
        val ref = db.collection("friendships").document(id)
        try {
            ref.set(
                mapOf(
                    "requesterId" to senderId,
                    "userIds" to listOf(senderId, receiverId),
                    "status" to "pending",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, ref.path)
            throw e
        }
    }

    suspend fun updateFriendshipStatus(friendshipId: String, status: String) {
        val ref = db.collection("friendships").document(friendshipId)
        try {
            ref.update(
                mapOf(
                    "status" to status,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, ref.path)
            throw e
        }
    }

    fun observeChallenges(userId: String): Flow<List<FriendChallenge>> = flow {
        db.collection("challenges")
            .whereArrayContains("participantIds", userId)
            .snapshots()
            .map { it.toObjects(FriendChallenge::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { error ->
                handleFlowError(error, OperationType.LIST, "challenges", userId)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun sendChallenge(
        senderId: String,
        receiverId: String,
        category: String,
        difficulty: String,
        questionCount: Long,
        senderScore: Long
    ) {
        val id = "ch_${UUID.randomUUID().toString().take(12)}"
        val ref = db.collection("challenges").document(id)
        try {
            ref.set(
                mapOf(
                    "senderId" to senderId,
                    "receiverId" to receiverId,
                    "participantIds" to listOf(senderId, receiverId),
                    "category" to category,
                    "difficulty" to difficulty,
                    "questionCount" to questionCount,
                    "status" to "pending",
                    "senderScore" to senderScore,
                    "receiverScore" to 0L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, ref.path)
            throw e
        }
    }

    suspend fun completeChallenge(challengeId: String, receiverScore: Long) {
        val ref = db.collection("challenges").document(challengeId)
        try {
            ref.update(
                mapOf(
                    "status" to "completed",
                    "receiverScore" to receiverScore,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, ref.path)
            throw e
        }
    }

    suspend fun submitReport(reporterId: String, targetId: String, reason: String) {
        val id = "rep_${UUID.randomUUID().toString().take(12)}"
        val ref = db.collection("reports").document(id)
        try {
            ref.set(
                mapOf(
                    "reporterId" to reporterId,
                    "targetId" to targetId,
                    "reason" to reason.take(280),
                    "status" to "open",
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        }
        catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, ref.path)
            throw e
        }
    }

    suspend fun deleteUserAccountData(userId: String) {
        val ref = db.collection("users").document(userId)
        try {
            ref.collection("private").document("info").delete().await()
            ref.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, ref.path)
            throw e
        }
    }

    private fun handleFlowError(error: Throwable, op: OperationType, path: String, expectedUid: String) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) return
        val ex = firestoreError ?: (error as? Exception)
        if (ex != null && Firebase.auth.currentUser?.uid == expectedUid) {
            handleFirestoreError(ex, op, path)
        }
    }
}
