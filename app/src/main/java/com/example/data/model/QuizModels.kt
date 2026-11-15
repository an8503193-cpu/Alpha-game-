package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val pseudo: String = "Joueur",
    val avatarId: String = "avatar_classic_spark",
    val frameId: String = "frame_none",
    val themeId: String = "dark_gaming",
    val titleId: String = "title_rookie",
    val level: Long = 1L,
    val xp: Long = 0L,
    val quizCoins: Long = 250L,
    val wins: Long = 0L,
    val gamesPlayed: Long = 0L,
    val correctAnswers: Long = 0L,
    val totalAnswers: Long = 0L,
    val bestScore: Long = 0L,
    val streakDays: Long = 1L,
    val lastLoginDate: String = "",
    val division: String = "Bronze",
    val country: String = "SN",
    val isPublic: Boolean = true,
    val showStats: Boolean = true,
    val allowInvites: Boolean = true,
    val reactionsEnabled: Boolean = true,
    val isMinorSafe: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PrivateUserInfo(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MatchHistory(
    @DocumentId val id: String = "",
    val userId: String = "",
    val mode: String = "",
    val category: String = "",
    val score: Long = 0L,
    val rank: Long = 1L,
    val correctCount: Long = 0L,
    val totalCount: Long = 0L,
    val xpEarned: Long = 0L,
    val coinsEarned: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ReviewItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val questionId: String = "",
    val category: String = "",
    val missedCount: Long = 0L,
    val isFavorite: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MissionProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val missionKey: String = "",
    val period: String = "daily",
    val progress: Long = 0L,
    val target: Long = 1L,
    val claimed: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class GameRoom(
    @DocumentId val id: String = "",
    val roomCode: String = "",
    val hostId: String = "",
    val mode: String = "Duel",
    val category: String = "Culture générale",
    val difficulty: String = "Moyen",
    val questionCount: Long = 10L,
    val timePerQuestionSec: Long = 15L,
    val status: String = "waiting",
    val maxPlayers: Long = 2L,
    val participantIds: List<String> = emptyList(),
    val participants: Map<String, Map<String, Any>> = emptyMap(),
    val currentQuestionIndex: Long = 0L,
    val questionIds: List<String> = emptyList(),
    val reactions: List<Map<String, Any>> = emptyList(),
    val messages: List<Map<String, Any>> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class LeaderboardEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val pseudo: String = "",
    val avatarId: String = "avatar_classic_spark",
    val division: String = "Bronze",
    val country: String = "SN",
    val seasonPoints: Long = 0L,
    val weeklyPoints: Long = 0L,
    val monthlyPoints: Long = 0L,
    val wins: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FriendChallenge(
    @DocumentId val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val participantIds: List<String> = emptyList(),
    val category: String = "",
    val difficulty: String = "Moyen",
    val questionCount: Long = 10L,
    val status: String = "pending",
    val senderScore: Long = 0L,
    val receiverScore: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Friendship(
    @DocumentId val id: String = "",
    val requesterId: String = "",
    val userIds: List<String> = emptyList(),
    val status: String = "pending",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ModerationReport(
    @DocumentId val id: String = "",
    val reporterId: String = "",
    val targetId: String = "",
    val reason: String = "",
    val status: String = "open",
    @ServerTimestamp val createdAt: Timestamp? = null
)
