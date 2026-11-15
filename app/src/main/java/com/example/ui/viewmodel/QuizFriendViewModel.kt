package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundEngine
import com.example.data.ai.AiCoachDiagnosis
import com.example.data.ai.AiCoachRepository
import com.example.data.model.AppNotification
import com.example.data.model.BotDifficulty
import com.example.data.model.DifficultyLevel
import com.example.data.model.FriendChallenge
import com.example.data.model.Friendship
import com.example.data.model.GameMode
import com.example.data.model.GameRoom
import com.example.data.model.LeaderboardEntry
import com.example.data.model.MatchHistory
import com.example.data.model.MissionProgress
import com.example.data.model.QuizCatalogs
import com.example.data.model.QuizQuestion
import com.example.data.model.ReviewItem
import com.example.data.model.ShopItem
import com.example.data.model.ShopItemType
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.prefs.AppPreferences
import com.example.data.prefs.PreferencesRepository
import com.example.data.questions.QuestionBank
import com.example.data.repository.QuizFriendRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class QuizConfig(
    val mode: GameMode = GameMode.DUEL,
    val category: String = "Toutes",
    val difficulty: DifficultyLevel = DifficultyLevel.MIXED,
    val questionCount: Int = 10,
    val timePerQuestionSec: Int = 15,
    val botDifficulty: BotDifficulty = BotDifficulty.AMATEUR,
    val activeRoomId: String? = null,
    val activeChallengeId: String? = null,
    val isSpectator: Boolean = false
)

data class PlayerAnswerRecord(
    val question: QuizQuestion,
    val chosenIndex: Int?,
    val isCorrect: Boolean,
    val pointsEarned: Int,
    val responseTimeMs: Long
)

data class ParticipantResult(
    val name: String,
    val avatarEmoji: String,
    val score: Int,
    val isBot: Boolean,
    val isCurrentUser: Boolean,
    val teamName: String? = null
)

data class ActiveReactionBubble(
    val id: Long,
    val senderName: String,
    val emoji: String
)

data class ActiveQuizSession(
    val config: QuizConfig,
    val questions: List<QuizQuestion>,
    val currentIndex: Int = 0,
    val remainingSec: Int = 15,
    val score: Int = 0,
    val correctCount: Int = 0,
    val currentStreak: Int = 0,
    val selectedOptionIndex: Int? = null,
    val answerRevealed: Boolean = false,
    val botScores: Map<String, Int> = emptyMap(),
    val records: List<PlayerAnswerRecord> = emptyList(),
    val reactions: List<ActiveReactionBubble> = emptyList(),
    val reactionCounts: Map<String, Int> = emptyMap(),
    val isFinished: Boolean = false,
    val finalRankings: List<ParticipantResult> = emptyList(),
    val xpEarned: Long = 0L,
    val coinsEarned: Long = 0L,
    val showPostGameAdBanner: Boolean = false
)

sealed interface AiCoachUiState {
    data object Idle : AiCoachUiState
    data object Loading : AiCoachUiState
    data class DiagnosisReady(val diagnosis: AiCoachDiagnosis) : AiCoachUiState
    data class TrainingQuestionReady(val question: QuizQuestion) : AiCoachUiState
    data class StreamingText(val text: String) : AiCoachUiState
    data class Error(val message: String) : AiCoachUiState
}

class QuizFriendViewModel(
    private val repository: QuizFriendRepository,
    private val aiCoachRepository: AiCoachRepository,
    private val prefsRepository: PreferencesRepository,
    val currentUserId: String,
    val userDisplayName: String?,
    val userEmail: String?
) : ViewModel() {

    val isAdminUser: Boolean = userEmail.equals("an8503193@gmail.com", ignoreCase = true)

    val preferences: StateFlow<AppPreferences> = prefsRepository.preferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), AppPreferences())

    val profileState: StateFlow<UiState<UserProfile>> = repository.observeUserProfile(currentUserId)
        .map<UserProfile?, UiState<UserProfile>> { profile ->
            if (profile != null) UiState.Success(profile) else UiState.Loading
        }
        .catchFirestore("Impossible de charger le profil")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val leaderboardState: StateFlow<UiState<List<LeaderboardEntry>>> = repository.observeLeaderboard()
        .map<List<LeaderboardEntry>, UiState<List<LeaderboardEntry>>> { UiState.Success(it) }
        .catchFirestore("Impossible de charger le classement")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val historyState: StateFlow<UiState<List<MatchHistory>>> = repository.observeMatchHistory(currentUserId)
        .map<List<MatchHistory>, UiState<List<MatchHistory>>> { UiState.Success(it) }
        .catchFirestore("Impossible de charger l'historique")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val reviewItemsState: StateFlow<UiState<List<ReviewItem>>> = repository.observeReviewItems(currentUserId)
        .map<List<ReviewItem>, UiState<List<ReviewItem>>> { UiState.Success(it) }
        .catchFirestore("Impossible de charger la révision")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val missionsState: StateFlow<UiState<List<MissionProgress>>> = repository.observeMissions(currentUserId)
        .map<List<MissionProgress>, UiState<List<MissionProgress>>> { UiState.Success(it) }
        .catchFirestore("Impossible de charger les missions")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val openRoomsState: StateFlow<UiState<List<GameRoom>>> = repository.observeOpenRooms()
        .map<List<GameRoom>, UiState<List<GameRoom>>> { UiState.Success(it) }
        .catchFirestore("Impossible de charger les salons")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val friendshipsState: StateFlow<UiState<List<Friendship>>> = repository.observeFriendships(currentUserId)
        .map<List<Friendship>, UiState<List<Friendship>>> { UiState.Success(it) }
        .catchFirestore("Impossible de charger les amis")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val publicPlayersState: StateFlow<UiState<List<UserProfile>>> = repository.observePublicPlayers()
        .map<List<UserProfile>, UiState<List<UserProfile>>> { list ->
            UiState.Success(list.filter { it.userId != currentUserId })
        }
        .catchFirestore("Impossible de charger les joueurs")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val challengesState: StateFlow<UiState<List<FriendChallenge>>> = repository.observeChallenges(currentUserId)
        .map<List<FriendChallenge>, UiState<List<FriendChallenge>>> { UiState.Success(it) }
        .catchFirestore("Impossible de charger les défis")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    private val _activeRoom = MutableStateFlow<GameRoom?>(null)
    val activeRoom: StateFlow<GameRoom?> = _activeRoom.asStateFlow()

    private val _quizSession = MutableStateFlow<ActiveQuizSession?>(null)
    val quizSession: StateFlow<ActiveQuizSession?> = _quizSession.asStateFlow()

    private val _aiCoachState = MutableStateFlow<AiCoachUiState>(AiCoachUiState.Idle)
    val aiCoachState: StateFlow<AiCoachUiState> = _aiCoachState.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    private val _mutedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val mutedUserIds: StateFlow<Set<String>> = _mutedUserIds.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            AppNotification("n1", "Bienvenue sur QuizFriend", "Votre bonus de connexion quotidienne est actif !", "reward", "Aujourd'hui"),
            AppNotification("n2", "Quiz du Jour disponible", "10 nouvelles questions synchronisées pour tous les joueurs.", "tournament", "Aujourd'hui"),
            AppNotification("n3", "Tournoi Semaine des Sciences", "Qualifications ouvertes • Trophée Einstein à débloquer.", "tournament", "En cours")
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _previewThemeId = MutableStateFlow<String?>(null)
    val previewThemeId: StateFlow<String?> = _previewThemeId.asStateFlow()

    private var timerJob: Job? = null
    private var roomJob: Job? = null
    private var lastReactionTimestampMs = 0L

    init {
        viewModelScope.launch {
            try {
                repository.ensureUserProfile(currentUserId, userDisplayName, userEmail)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Profile bootstrap failed", e)
                _statusBanner.value = "Erreur de synchronisation du profil : ${e.localizedMessage}"
            }
        }
        viewModelScope.launch {
            preferences.collect { prefs ->
                SoundEngine.sfxEnabled = prefs.sfxEnabled
                SoundEngine.sfxVolume = prefs.sfxVolume
                SoundEngine.updateMusicState(prefs.musicEnabled, prefs.musicVolume, prefs.musicAmbiance)
            }
        }
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun markNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(unread = false) } }
    }

    fun setThemePreview(themeId: String?) {
        _previewThemeId.value = themeId
    }

    fun applyTheme(themeId: String) {
        _previewThemeId.value = null
        viewModelScope.launch {
            prefsRepository.setThemeId(themeId)
            val profile = currentProfile() ?: return@launch
            runCatching { repository.updateProfileFields(currentUserId, mapOf("themeId" to themeId)) }
            _statusBanner.value = "Thème appliqué !"
        }
    }

    fun updatePseudo(newPseudo: String) {
        val clean = newPseudo.trim()
        if (clean.length !in 2..24) {
            _statusBanner.value = "Le pseudo doit contenir entre 2 et 24 caractères."
            return
        }
        viewModelScope.launch {
            val profile = currentProfile() ?: return@launch
            try {
                repository.updateProfileFields(currentUserId, mapOf("pseudo" to clean))
                repository.syncLeaderboardEntry(
                    currentUserId, clean, profile.avatarId, profile.division, profile.country,
                    (profile.xp / 2L) + (profile.wins * 120L), profile.wins
                )
                _statusBanner.value = "Pseudo mis à jour : $clean"
            } catch (e: Exception) {
                _statusBanner.value = e.localizedMessage ?: "Erreur lors du changement de pseudo."
            }
        }
    }

    fun buyOrEquipShopItem(item: ShopItem) {
        viewModelScope.launch {
            val profile = currentProfile() ?: return@launch
            if (item.priceCoins > 0 && profile.quizCoins < item.priceCoins) {
                _statusBanner.value = "QuizCoins insuffisants (${item.priceCoins} requis)."
                return@launch
            }
            val updates = mutableMapOf<String, Any>()
            when (item.type) {
                ShopItemType.AVATAR -> updates["avatarId"] = item.id
                ShopItemType.FRAME -> updates["frameId"] = item.id
                ShopItemType.THEME -> {
                    updates["themeId"] = item.id
                    prefsRepository.setThemeId(item.id)
                }
                ShopItemType.TITLE -> updates["titleId"] = item.id
                ShopItemType.VICTORY_EFFECT, ShopItemType.DECORATION -> Unit
            }
            if (item.priceCoins > 0) {
                updates["quizCoins"] = (profile.quizCoins - item.priceCoins).coerceAtLeast(0L)
            }
            if (updates.isNotEmpty()) {
                repository.updateProfileFields(currentUserId, updates)
            }
            _statusBanner.value = "${item.name} activé !"
        }
    }

    fun claimMission(mission: MissionProgress) {
        viewModelScope.launch {
            val profile = currentProfile() ?: return@launch
            if (mission.claimed || mission.progress < mission.target) return@launch
            try {
                repository.claimMission(currentUserId, mission.missionKey, profile, 120L, 75L)
                _statusBanner.value = "Mission réclamée : +120 XP et +75 QuizCoins !"
                SoundEngine.playVictory()
            } catch (e: Exception) {
                _statusBanner.value = e.localizedMessage
            }
        }
    }

    fun grantOptionalRewardedCoins() {
        viewModelScope.launch {
            val profile = currentProfile() ?: return@launch
            repository.updateProfileFields(currentUserId, mapOf("quizCoins" to profile.quizCoins + 50L))
            _statusBanner.value = "+50 QuizCoins ajoutés (aperçu récompense test) !"
        }
    }

    // --- MULTIPLAYER ROOMS ---
    fun createPrivateRoom(config: QuizConfig, maxPlayers: Int) {
        viewModelScope.launch {
            val profile = currentProfile() ?: return@launch
            val questions = QuestionBank.selectQuestions(config.category, config.difficulty, config.questionCount)
            try {
                val roomId = repository.createRoom(
                    hostProfile = profile,
                    mode = config.mode.id,
                    category = config.category,
                    difficulty = config.difficulty.label,
                    questionCount = config.questionCount.toLong(),
                    timePerQuestionSec = config.timePerQuestionSec.toLong(),
                    maxPlayers = maxPlayers.toLong(),
                    questionIds = questions.map { it.id }
                )
                attachToRoom(roomId)
            } catch (e: Exception) {
                _statusBanner.value = "Impossible de créer le salon : ${e.localizedMessage}"
            }
        }
    }

    fun joinRoomByCode(code: String) {
        val clean = code.trim().uppercase()
        if (clean.length != 6) {
            _statusBanner.value = "Entrez un code valide à 6 caractères."
            return
        }
        viewModelScope.launch {
            val profile = currentProfile() ?: return@launch
            try {
                val roomId = repository.joinRoomByCode(clean, profile)
                if (roomId == null) {
                    _statusBanner.value = "Salon introuvable, complet ou partie déjà commencée."
                } else {
                    attachToRoom(roomId)
                }
            } catch (e: Exception) {
                _statusBanner.value = "Erreur : ${e.localizedMessage}"
            }
        }
    }

    fun attachToRoom(roomId: String) {
        roomJob?.cancel()
        roomJob = viewModelScope.launch {
            repository.observeRoom(roomId).collect { room ->
                _activeRoom.value = room
                if (room != null && room.status == "active" && _quizSession.value == null) {
                    val qs = room.questionIds.mapNotNull { QuestionBank.findById(it) }
                        .ifEmpty { QuestionBank.selectQuestions(room.category, DifficultyLevel.MIXED, room.questionCount.toInt()) }
                    val mode = GameMode.entries.firstOrNull { it.id == room.mode } ?: GameMode.DUEL
                    val isSpec = !room.participantIds.contains(currentUserId)
                    startSessionWithQuestions(
                        config = QuizConfig(
                            mode = mode,
                            category = room.category,
                            questionCount = qs.size,
                            timePerQuestionSec = room.timePerQuestionSec.toInt(),
                            activeRoomId = room.id,
                            isSpectator = isSpec
                        ),
                        questions = qs
                    )
                }
            }
        }
    }

    fun toggleReadyInRoom() {
        val room = _activeRoom.value ?: return
        val profile = currentProfile() ?: return
        viewModelScope.launch {
            val currentMap = room.participants.toMutableMap()
            val mine = mutableMapOf<String, Any>().apply {
                val existing = currentMap[currentUserId]
                if (existing != null) {
                    putAll(existing)
                } else {
                    put("pseudo", profile.pseudo)
                    put("avatarId", profile.avatarId)
                    put("score", 0L)
                    put("isBot", false)
                }
                val wasReady = get("ready") as? Boolean ?: false
                put("ready", !wasReady)
            }
            currentMap[currentUserId] = mine
            repository.updateRoomFields(room.id, mapOf("participants" to currentMap))
        }
    }

    fun startRoomMatch() {
        val room = _activeRoom.value ?: return
        if (room.hostId != currentUserId) return
        viewModelScope.launch {
            repository.updateRoomFields(room.id, mapOf("status" to "active"))
        }
    }

    fun sendRoomChatMessage(text: String) {
        val clean = text.trim().take(120)
        if (clean.isEmpty()) return
        val room = _activeRoom.value ?: return
        val profile = currentProfile() ?: return
        viewModelScope.launch {
            val msgs = (room.messages + mapOf(
                "senderId" to currentUserId,
                "sender" to profile.pseudo,
                "text" to clean
            )).takeLast(25)
            repository.updateRoomFields(room.id, mapOf("messages" to msgs))
        }
    }

    fun leaveRoom() {
        roomJob?.cancel()
        _activeRoom.value = null
    }

    // --- QUIZ SESSION ENGINE ---
    fun startQuiz(config: QuizConfig) {
        val qs = when (config.mode) {
            GameMode.DAILY_QUIZ -> QuestionBank.getDailyQuizQuestions(10)
            GameMode.QUICK_ANSWER -> QuestionBank.selectQuestions(config.category, config.difficulty, config.questionCount)
            else -> QuestionBank.selectQuestions(config.category, config.difficulty, config.questionCount)
        }
        startSessionWithQuestions(config, qs)
    }

    fun startRevisionQuiz(reviewItems: List<ReviewItem>) {
        val qs = reviewItems.mapNotNull { QuestionBank.findById(it.questionId) }
            .ifEmpty { QuestionBank.selectQuestions("Toutes", DifficultyLevel.EASY, 10) }
        startSessionWithQuestions(
            QuizConfig(
                mode = GameMode.TRAINING,
                category = "Révision",
                difficulty = DifficultyLevel.MIXED,
                questionCount = qs.size,
                timePerQuestionSec = 0
            ),
            qs
        )
    }

    private fun startSessionWithQuestions(config: QuizConfig, questions: List<QuizQuestion>) {
        val initialBots = when (config.mode) {
            GameMode.DUEL, GameMode.RANDOM -> mapOf(config.botDifficulty.pseudo to 0)
            GameMode.TRIO -> mapOf(config.botDifficulty.pseudo to 0, BotDifficulty.BEGINNER.pseudo to 0)
            GameMode.TEAM -> mapOf("Équipe Adversaire [IA]" to 0, "Coéquipier [IA]" to 0)
            else -> emptyMap()
        }
        _quizSession.value = ActiveQuizSession(
            config = config,
            questions = questions,
            currentIndex = 0,
            remainingSec = config.timePerQuestionSec,
            botScores = initialBots
        )
        startQuestionTimer()
    }

    private fun startQuestionTimer() {
        timerJob?.cancel()
        val session = _quizSession.value ?: return
        if (session.isFinished || session.config.timePerQuestionSec <= 0) return
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _quizSession.value ?: break
                if (current.answerRevealed || current.isFinished) break
                if (current.remainingSec <= 1) {
                    submitAnswer(optionIndex = null)
                    break
                } else {
                    val nextSec = current.remainingSec - 1
                    if (nextSec <= 3) SoundEngine.playTick()
                    _quizSession.update { it?.copy(remainingSec = nextSec) }
                }
            }
        }
    }

    fun submitAnswer(optionIndex: Int?) {
        val session = _quizSession.value ?: return
        if (session.answerRevealed || session.isFinished) return
        timerJob?.cancel()

        val question = session.questions[session.currentIndex]
        val isCorrect = optionIndex != null && optionIndex == question.correctIndex
        val maxSec = session.config.timePerQuestionSec.coerceAtLeast(1)
        val speedBonus = if (isCorrect && session.config.timePerQuestionSec > 0) {
            ((session.remainingSec.toFloat() / maxSec.toFloat()) * 50f).toInt().coerceIn(0, 50)
        } else if (isCorrect) 25 else 0
        val earned = if (isCorrect) 100 + speedBonus else 0
        val elapsedMs = if (session.config.timePerQuestionSec > 0) {
            (session.config.timePerQuestionSec - session.remainingSec) * 1000L
        } else 2500L

        if (isCorrect) SoundEngine.playCorrect() else SoundEngine.playWrong()

        val updatedBots = session.botScores.mapValues { (_, prevScore) ->
            val bot = BotDifficulty.entries.firstOrNull {
                session.config.botDifficulty.pseudo == it.pseudo
            } ?: session.config.botDifficulty
            val botCorrect = Math.random() < bot.accuracyRate
            val botBonus = if (botCorrect) (15..45).random() else 0
            prevScore + if (botCorrect) 100 + botBonus else 0
        }

        val record = PlayerAnswerRecord(
            question = question,
            chosenIndex = optionIndex,
            isCorrect = isCorrect,
            pointsEarned = earned,
            responseTimeMs = elapsedMs
        )

        _quizSession.update { cur ->
            cur?.copy(
                score = cur.score + earned,
                correctCount = cur.correctCount + if (isCorrect) 1 else 0,
                currentStreak = if (isCorrect) cur.currentStreak + 1 else 0,
                selectedOptionIndex = optionIndex,
                answerRevealed = true,
                botScores = updatedBots,
                records = cur.records + record
            )
        }

        val roomId = session.config.activeRoomId
        val profile = currentProfile()
        if (roomId != null && profile != null && !session.config.isSpectator) {
            viewModelScope.launch {
                val room = _activeRoom.value ?: return@launch
                val pMap = room.participants.toMutableMap()
                val mine = mutableMapOf<String, Any>().apply {
                    val existing = pMap[currentUserId]
                    if (existing != null) {
                        putAll(existing)
                    } else {
                        put("pseudo", profile.pseudo)
                        put("avatarId", profile.avatarId)
                    }
                    put("score", (session.score + earned).toLong())
                }
                pMap[currentUserId] = mine
                runCatching { repository.updateRoomFields(roomId, mapOf("participants" to pMap)) }
            }
        }
    }

    fun nextQuestion() {
        val session = _quizSession.value ?: return
        if (session.currentIndex + 1 < session.questions.size) {
            _quizSession.update {
                it?.copy(
                    currentIndex = it.currentIndex + 1,
                    remainingSec = it.config.timePerQuestionSec,
                    selectedOptionIndex = null,
                    answerRevealed = false
                )
            }
            startQuestionTimer()
        } else {
            finishQuizSession()
        }
    }

    private fun finishQuizSession() {
        val session = _quizSession.value ?: return
        val profile = currentProfile()
        val myEmoji = QuizCatalogs.avatarEmojiFor(profile?.avatarId ?: "avatar_classic_spark")
        val myName = profile?.pseudo ?: "Vous"

        val rankings = buildList {
            if (!session.config.isSpectator) {
                add(
                    ParticipantResult(
                        name = myName,
                        avatarEmoji = myEmoji,
                        score = session.score,
                        isBot = false,
                        isCurrentUser = true,
                        teamName = if (session.config.mode == GameMode.TEAM) "Équipe Alpha" else null
                    )
                )
            }
            session.botScores.forEach { (botName, botScore) ->
                val botEmoji = BotDifficulty.entries.firstOrNull { it.pseudo == botName }?.avatarEmoji ?: "🤖"
                add(
                    ParticipantResult(
                        name = botName,
                        avatarEmoji = botEmoji,
                        score = botScore,
                        isBot = true,
                        isCurrentUser = false,
                        teamName = if (session.config.mode == GameMode.TEAM && botName.contains("Coéquipier")) "Équipe Alpha"
                        else if (session.config.mode == GameMode.TEAM) "Équipe Oméga" else null
                    )
                )
            }
        }.sortedByDescending { it.score }

        val myRank = (rankings.indexOfFirst { it.isCurrentUser } + 1).coerceAtLeast(1)
        val won = myRank == 1 && session.correctCount > 0
        val xpEarned = 35L + (session.correctCount * 15L) + if (won) 60L else 0L
        val coinsEarned = 15L + (session.correctCount * 5L) + if (won) 40L else 0L

        if (won) SoundEngine.playVictory()

        viewModelScope.launch {
            val prefs = preferences.value
            val gamesCycle = prefsRepository.incrementCompletedGamesSinceAd()
            val showBannerAd = prefs.adsEnabled && !prefs.adFreeUnlocked && gamesCycle == 0

            _quizSession.update {
                it?.copy(
                    isFinished = true,
                    finalRankings = rankings,
                    xpEarned = xpEarned,
                    coinsEarned = coinsEarned,
                    showPostGameAdBanner = showBannerAd
                )
            }

            if (profile != null && !session.config.isSpectator) {
                val missed = session.records.filter { !it.isCorrect }
                    .map { it.question.id to it.question.category }
                runCatching {
                    repository.recordMatchResult(
                        userId = currentUserId,
                        profile = profile,
                        mode = session.config.mode.id,
                        category = session.config.category,
                        score = session.score.toLong(),
                        rank = myRank.toLong(),
                        correctCount = session.correctCount.toLong(),
                        totalCount = session.questions.size.toLong(),
                        isRanked = session.config.mode.isRanked,
                        missedQuestionIds = missed,
                        xpEarned = xpEarned,
                        coinsEarned = coinsEarned
                    )
                }
                session.config.activeChallengeId?.let { chId ->
                    runCatching { repository.completeChallenge(chId, session.score.toLong()) }
                }
                session.config.activeRoomId?.let { rId ->
                    runCatching { repository.updateRoomFields(rId, mapOf("status" to "finished")) }
                }
            }
        }
    }

    fun closeQuizSession() {
        timerJob?.cancel()
        _quizSession.value = null
    }

    fun sendQuizReaction(emoji: String) {
        val profile = currentProfile()
        if (profile?.reactionsEnabled == false) return
        val now = System.currentTimeMillis()
        if (now - lastReactionTimestampMs < 1200L) return
        lastReactionTimestampMs = now
        SoundEngine.playReaction()

        val bubble = ActiveReactionBubble(now, profile?.pseudo ?: "Moi", emoji)
        _quizSession.update { cur ->
            if (cur == null) return@update null
            val nextCounts = cur.reactionCounts.toMutableMap()
            nextCounts[emoji] = (nextCounts[emoji] ?: 0) + 1
            cur.copy(
                reactions = (cur.reactions + bubble).takeLast(5),
                reactionCounts = nextCounts
            )
        }
        viewModelScope.launch {
            delay(2500L)
            _quizSession.update { cur ->
                cur?.copy(reactions = cur.reactions.filterNot { it.id == bubble.id })
            }
        }
    }

    fun toggleFavoriteQuestion(question: QuizQuestion, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteQuestion(currentUserId, question.id, question.category, isFav)
            _statusBanner.value = if (isFav) "Ajoutée aux questions favorites !" else "Retirée des favoris."
        }
    }

    // --- FRIENDS, CHALLENGES, SAFETY ---
    fun sendFriendRequest(targetUserId: String) {
        viewModelScope.launch {
            runCatching { repository.sendFriendRequest(currentUserId, targetUserId) }
                .onSuccess { _statusBanner.value = "Invitation d'ami envoyée !" }
                .onFailure { _statusBanner.value = it.localizedMessage }
        }
    }

    fun acceptFriendRequest(friendshipId: String) {
        viewModelScope.launch {
            runCatching { repository.updateFriendshipStatus(friendshipId, "accepted") }
                .onSuccess { _statusBanner.value = "Ami ajouté !" }
        }
    }

    fun blockPlayer(targetUserId: String) {
        _blockedUserIds.update { it + targetUserId }
        _statusBanner.value = "Joueur bloqué."
    }

    fun mutePlayer(targetUserId: String) {
        _mutedUserIds.update { it + targetUserId }
        _statusBanner.value = "Joueur mis en sourdine."
    }

    fun reportPlayer(targetUserId: String, reason: String) {
        viewModelScope.launch {
            runCatching { repository.submitReport(currentUserId, targetUserId, reason) }
                .onSuccess { _statusBanner.value = "Signalement enregistré. Merci pour votre vigilance." }
                .onFailure { _statusBanner.value = it.localizedMessage }
        }
    }

    fun sendChallengeToFriend(receiverId: String, category: String, difficulty: DifficultyLevel, count: Int) {
        viewModelScope.launch {
            runCatching {
                repository.sendChallenge(currentUserId, receiverId, category, difficulty.label, count.toLong(), 0L)
            }.onSuccess {
                _statusBanner.value = "Défi envoyé !"
            }.onFailure {
                _statusBanner.value = it.localizedMessage
            }
        }
    }

    fun sendVirtualGift(gift: VirtualGift, recipientName: String) {
        viewModelScope.launch {
            val profile = currentProfile() ?: return@launch
            if (profile.quizCoins < gift.costCoins) {
                _statusBanner.value = "QuizCoins insuffisants pour offrir ${gift.name}."
                return@launch
            }
            repository.updateProfileFields(currentUserId, mapOf("quizCoins" to profile.quizCoins - gift.costCoins))
            _statusBanner.value = "${gift.emoji} ${gift.name} envoyé à $recipientName !"
        }
    }

    // --- AI COACH ---
    fun runAiCoachDiagnosis() {
        val profile = currentProfile() ?: return
        val history = (historyState.value as? UiState.Success)?.data.orEmpty()
        val catCounts = history.groupBy { it.category }.mapValues { (_, list) ->
            val corr = list.sumOf { it.correctCount }
            val tot = list.sumOf { it.totalCount }.coerceAtLeast(1L)
            (corr * 100 / tot).toInt()
        }
        val strong = catCounts.filterValues { it >= 70 }.keys.toList()
        val weak = catCounts.filterValues { it < 70 }.keys.toList()
        val winRate = if (profile.gamesPlayed > 0) ((profile.wins * 100) / profile.gamesPlayed).toInt() else 0
        val acc = if (profile.totalAnswers > 0) ((profile.correctAnswers * 100) / profile.totalAnswers).toInt() else 0

        _aiCoachState.value = AiCoachUiState.Loading
        viewModelScope.launch {
            aiCoachRepository.analyzePlayerPerformance(
                pseudo = profile.pseudo,
                level = profile.level,
                winRatePercent = winRate,
                accuracyPercent = acc,
                weakCategories = weak,
                strongCategories = strong
            ).onSuccess {
                _aiCoachState.value = AiCoachUiState.DiagnosisReady(it)
            }.onFailure {
                _aiCoachState.value = AiCoachUiState.Error(it.localizedMessage ?: "Erreur du Coach IA.")
            }
        }
    }

    fun generateAiCoachExercise(category: String, difficulty: DifficultyLevel) {
        _aiCoachState.value = AiCoachUiState.Loading
        viewModelScope.launch {
            aiCoachRepository.generateTrainingExercise(category, difficulty)
                .onSuccess { _aiCoachState.value = AiCoachUiState.TrainingQuestionReady(it) }
                .onFailure { _aiCoachState.value = AiCoachUiState.Error(it.localizedMessage ?: "Échec de génération.") }
        }
    }

    fun explainWrongQuestionWithAi(question: QuizQuestion, chosenOption: String?) {
        _aiCoachState.value = AiCoachUiState.Loading
        viewModelScope.launch {
            try {
                aiCoachRepository.streamWrongAnswerExplanation(question, chosenOption).collect { text ->
                    _aiCoachState.value = AiCoachUiState.StreamingText(text)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _aiCoachState.value = AiCoachUiState.Error(e.localizedMessage ?: "Erreur lors de l'explication.")
            }
        }
    }

    // --- SETTINGS ---
    fun updateAudioSettings(
        musicEnabled: Boolean? = null,
        sfxEnabled: Boolean? = null,
        musicVol: Float? = null,
        sfxVol: Float? = null,
        ambiance: String? = null
    ) {
        viewModelScope.launch {
            musicEnabled?.let { prefsRepository.setMusicEnabled(it) }
            sfxEnabled?.let { prefsRepository.setSfxEnabled(it) }
            musicVol?.let { prefsRepository.setMusicVolume(it) }
            sfxVol?.let { prefsRepository.setSfxVolume(it) }
            ambiance?.let { prefsRepository.setMusicAmbiance(it) }
        }
    }

    fun updatePrivacySettings(
        isPublic: Boolean? = null,
        showStats: Boolean? = null,
        allowInvites: Boolean? = null,
        reactionsEnabled: Boolean? = null,
        isMinorSafe: Boolean? = null
    ) {
        viewModelScope.launch {
            val map = mutableMapOf<String, Any>()
            isPublic?.let { map["isPublic"] = it }
            showStats?.let { map["showStats"] = it }
            allowInvites?.let { map["allowInvites"] = it }
            reactionsEnabled?.let { map["reactionsEnabled"] = it }
            isMinorSafe?.let { map["isMinorSafe"] = it }
            if (map.isNotEmpty()) repository.updateProfileFields(currentUserId, map)
        }
    }

    fun updateAppSettings(animations: Boolean? = null, notifications: Boolean? = null, adsEnabled: Boolean? = null) {
        viewModelScope.launch {
            animations?.let { prefsRepository.setAnimationsEnabled(it) }
            notifications?.let { prefsRepository.setNotificationsEnabled(it) }
            adsEnabled?.let { prefsRepository.setAdsEnabled(it) }
        }
    }

    fun deleteAccount(onComplete: () -> Unit) {
        viewModelScope.launch {
            runCatching { repository.deleteUserAccountData(currentUserId) }
            onComplete()
        }
    }

    fun currentProfile(): UserProfile? = (profileState.value as? UiState.Success)?.data

    private fun <T> Flow<UiState<T>>.catchFirestore(fallbackMsg: String): Flow<UiState<T>> =
        catch { error ->
            val fsError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (fsError == null && error is CancellationException) throw error
            val root = fsError ?: error
            Log.w(TAG, fallbackMsg, root)
            emit(UiState.Error(root.localizedMessage ?: fallbackMsg))
        }

    private companion object {
        const val TAG = "QuizFriendVM"
    }
}
