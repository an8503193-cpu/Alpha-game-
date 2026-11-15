package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appcheck.configureAppCheck
import com.example.data.ai.AiCoachRepository
import com.example.data.model.QuizThemeStyle
import com.example.data.prefs.PreferencesRepository
import com.example.data.repository.QuizFriendRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOutUser
import com.example.ui.components.QuizFriendTopBar
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MissionsOverlayScreen
import com.example.ui.screens.PlayHubScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizPlayScreen
import com.example.ui.screens.QuizResultScreen
import com.example.ui.screens.RevisionScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.NotificationsDialog
import com.example.ui.theme.QuizFriendTheme
import com.example.ui.viewmodel.QuizFriendViewModel
import com.example.ui.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private data class NavTab(val label: String, val icon: ImageVector, val tag: String)

private val mainTabs = listOf(
    NavTab("Accueil", Icons.Default.Home, "nav_home"),
    NavTab("Jouer", Icons.Default.PlayArrow, "nav_play"),
    NavTab("Classements", Icons.Default.EmojiEvents, "nav_leaderboard"),
    NavTab("Amis", Icons.Default.Groups, "nav_friends"),
    NavTab("Boutique", Icons.Default.Storefront, "nav_shop"),
    NavTab("Profil", Icons.Default.Person, "nav_profile")
)

private fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            val currentUser by Firebase.auth.authStateFlow()
                .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)

            val user = currentUser
            if (user == null) {
                QuizFriendTheme(QuizThemeStyle.DARK_GAMING) {
                    AuthScreen(onAuthSuccess = {})
                }
            } else {
                key(user.uid) {
                    QuizFriendAppRoot(
                        application = application,
                        user = user
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizFriendAppRoot(
    application: Application,
    user: FirebaseUser
) {
    val vm: QuizFriendViewModel = viewModel(
        key = user.uid,
        factory = viewModelFactory {
            initializer {
                QuizFriendViewModel(
                    repository = QuizFriendRepository(application),
                    aiCoachRepository = AiCoachRepository(),
                    prefsRepository = PreferencesRepository(application),
                    currentUserId = user.uid,
                    userDisplayName = user.displayName,
                    userEmail = user.email
                )
            }
        }
    )

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val preferences by vm.preferences.collectAsStateWithLifecycle()
    val previewThemeId by vm.previewThemeId.collectAsStateWithLifecycle()
    val profileState by vm.profileState.collectAsStateWithLifecycle()
    val leaderboardState by vm.leaderboardState.collectAsStateWithLifecycle()
    val historyState by vm.historyState.collectAsStateWithLifecycle()
    val reviewItemsState by vm.reviewItemsState.collectAsStateWithLifecycle()
    val missionsState by vm.missionsState.collectAsStateWithLifecycle()
    val openRoomsState by vm.openRoomsState.collectAsStateWithLifecycle()
    val friendshipsState by vm.friendshipsState.collectAsStateWithLifecycle()
    val publicPlayersState by vm.publicPlayersState.collectAsStateWithLifecycle()
    val challengesState by vm.challengesState.collectAsStateWithLifecycle()
    val activeRoom by vm.activeRoom.collectAsStateWithLifecycle()
    val quizSession by vm.quizSession.collectAsStateWithLifecycle()
    val aiCoachState by vm.aiCoachState.collectAsStateWithLifecycle()
    val statusBanner by vm.statusBanner.collectAsStateWithLifecycle()
    val blockedIds by vm.blockedUserIds.collectAsStateWithLifecycle()
    val mutedIds by vm.mutedUserIds.collectAsStateWithLifecycle()
    val notifications by vm.notifications.collectAsStateWithLifecycle()

    val profile = (profileState as? UiState.Success)?.data
    val leaderboard = (leaderboardState as? UiState.Success)?.data.orEmpty()
    val history = (historyState as? UiState.Success)?.data.orEmpty()
    val reviewItems = (reviewItemsState as? UiState.Success)?.data.orEmpty()
    val missions = (missionsState as? UiState.Success)?.data.orEmpty()
    val openRooms = (openRoomsState as? UiState.Success)?.data.orEmpty()
    val friendships = (friendshipsState as? UiState.Success)?.data.orEmpty()
    val publicPlayers = (publicPlayersState as? UiState.Success)?.data.orEmpty()
    val challenges = (challengesState as? UiState.Success)?.data.orEmpty()

    val activeTheme = QuizThemeStyle.fromId(previewThemeId ?: profile?.themeId ?: preferences.themeId)

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var activeModal by rememberSaveable { mutableStateOf<String?>(null) }
    var showNotifications by rememberSaveable { mutableStateOf(false) }

    QuizFriendTheme(themeStyle = activeTheme) {
        val activeQuiz = quizSession
        if (activeQuiz != null) {
            if (activeQuiz.isFinished) {
                QuizResultScreen(
                    session = activeQuiz,
                    profile = profile,
                    animationsEnabled = preferences.animationsEnabled,
                    onRematch = {
                        val cfg = activeQuiz.config
                        vm.closeQuizSession()
                        vm.startQuiz(cfg)
                    },
                    onReturnHome = { vm.closeQuizSession() }
                )
            } else {
                QuizPlayScreen(
                    session = activeQuiz,
                    reactionsEnabled = profile?.reactionsEnabled ?: true,
                    aiCoachState = aiCoachState,
                    onSelectOption = { vm.submitAnswer(it) },
                    onNextQuestion = { vm.nextQuestion() },
                    onSendReaction = { vm.sendQuizReaction(it) },
                    onToggleFavorite = { q, fav -> vm.toggleFavoriteQuestion(q, fav) },
                    onAskAiExplanation = { q, opt -> vm.explainWrongQuestionWithAi(q, opt) },
                    onExitQuiz = { vm.closeQuizSession() }
                )
            }
        } else {
            when (activeModal) {
                "coach" -> AiCoachScreen(
                    aiCoachState = aiCoachState,
                    onRunDiagnosis = { vm.runAiCoachDiagnosis() },
                    onGenerateExercise = { cat, diff -> vm.generateAiCoachExercise(cat, diff) },
                    onStartRecommendedTraining = { cfg ->
                        activeModal = null
                        vm.startQuiz(cfg)
                    },
                    onBack = { activeModal = null }
                )
                "revision" -> RevisionScreen(
                    reviewItems = reviewItems,
                    onStartRevisionSession = { items ->
                        activeModal = null
                        vm.startRevisionQuiz(items)
                    },
                    onBack = { activeModal = null }
                )
                "missions" -> MissionsOverlayScreen(
                    missions = missions,
                    onClaimMission = { vm.claimMission(it) },
                    onBack = { activeModal = null }
                )
                "settings" -> SettingsScreen(
                    profile = profile,
                    preferences = preferences,
                    isAdminUser = vm.isAdminUser,
                    blockedCount = blockedIds.size,
                    mutedCount = mutedIds.size,
                    onUpdatePseudo = { vm.updatePseudo(it) },
                    onApplyTheme = { vm.applyTheme(it) },
                    onUpdateAudio = { mEn, sEn, mVol, sVol, amb -> vm.updateAudioSettings(mEn, sEn, mVol, sVol, amb) },
                    onUpdatePrivacy = { pub, stats, inv, react, minor -> vm.updatePrivacySettings(pub, stats, inv, react, minor) },
                    onUpdateAppSettings = { anim, notif, ads -> vm.updateAppSettings(anim, notif, ads) },
                    onGrantTestRewardCoins = { vm.grantOptionalRewardedCoins() },
                    onSignOut = {
                        signOutUser(credentialManager, scope) {}
                    },
                    onDeleteAccount = {
                        vm.deleteAccount {
                            signOutUser(credentialManager, scope) {}
                        }
                    },
                    onBack = { activeModal = null }
                )
                else -> BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val useRail = maxWidth >= 600.dp
                    val unreadCount = notifications.count { it.unread }

                    Scaffold(
                        topBar = {
                            QuizFriendTopBar(
                                profile = profile,
                                unreadNotifications = unreadCount,
                                onOpenNotifications = { showNotifications = true },
                                onOpenSettings = { activeModal = "settings" },
                                onOpenProfile = { selectedTab = 5 }
                            )
                        },
                        bottomBar = {
                            if (!useRail) {
                                NavigationBar(windowInsets = WindowInsets.navigationBars) {
                                    mainTabs.forEachIndexed { index, tab ->
                                        NavigationBarItem(
                                            selected = selectedTab == index,
                                            onClick = { selectedTab = index },
                                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                                            label = { Text(tab.label, maxLines = 1) },
                                            modifier = Modifier.testTag(tab.tag)
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (useRail) {
                                NavigationRail(modifier = Modifier.fillMaxHeight()) {
                                    mainTabs.forEachIndexed { index, tab ->
                                        NavigationRailItem(
                                            selected = selectedTab == index,
                                            onClick = { selectedTab = index },
                                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                                            label = { Text(tab.label) },
                                            modifier = Modifier.testTag(tab.tag)
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Box(modifier = Modifier.widthIn(max = 920.dp).fillMaxSize()) {
                                    when (selectedTab) {
                                        0 -> HomeScreen(
                                            profile = profile,
                                            missions = missions,
                                            challenges = challenges,
                                            onNavigateTab = { selectedTab = it },
                                            onQuickStartQuiz = { vm.startQuiz(it) },
                                            onOpenAiCoach = { activeModal = "coach" },
                                            onOpenRevision = { activeModal = "revision" },
                                            onOpenMissions = { activeModal = "missions" }
                                        )
                                        1 -> PlayHubScreen(
                                            currentUserId = vm.currentUserId,
                                            activeRoom = activeRoom,
                                            openRooms = openRooms,
                                            onStartQuiz = { vm.startQuiz(it) },
                                            onCreatePrivateRoom = { cfg, maxP -> vm.createPrivateRoom(cfg, maxP) },
                                            onJoinRoomByCode = { vm.joinRoomByCode(it) },
                                            onAttachRoom = { vm.attachToRoom(it) },
                                            onToggleReady = { vm.toggleReadyInRoom() },
                                            onStartRoomMatch = { vm.startRoomMatch() },
                                            onSendRoomMessage = { vm.sendRoomChatMessage(it) },
                                            onLeaveRoom = { vm.leaveRoom() }
                                        )
                                        2 -> LeaderboardScreen(
                                            profile = profile,
                                            entries = leaderboard,
                                            friendIds = friendships.filter { it.status == "accepted" }
                                                .flatMap { it.userIds }
                                                .filter { it != vm.currentUserId }
                                                .toSet(),
                                            onJoinTournament = { vm.startQuiz(it) }
                                        )
                                        3 -> FriendsScreen(
                                            currentUserId = vm.currentUserId,
                                            publicPlayers = publicPlayers,
                                            friendships = friendships,
                                            challenges = challenges,
                                            blockedIds = blockedIds,
                                            onSendFriendRequest = { vm.sendFriendRequest(it) },
                                            onAcceptFriendRequest = { vm.acceptFriendRequest(it) },
                                            onSendChallenge = { id, cat, diff, cnt ->
                                                vm.sendChallengeToFriend(id, cat, diff, cnt)
                                            },
                                            onAcceptAndPlayChallenge = { vm.startQuiz(it) },
                                            onSendGift = { gift, target -> vm.sendVirtualGift(gift, target) },
                                            onBlockPlayer = { vm.blockPlayer(it) },
                                            onReportPlayer = { id, reason -> vm.reportPlayer(id, reason) }
                                        )
                                        4 -> ShopScreen(
                                            profile = profile,
                                            previewThemeId = previewThemeId,
                                            onPreviewTheme = { vm.setThemePreview(it) },
                                            onBuyOrEquip = { vm.buyOrEquipShopItem(it) },
                                            onWatchOptionalRewardedAd = { vm.grantOptionalRewardedCoins() }
                                        )
                                        else -> ProfileScreen(
                                            profile = profile,
                                            history = history,
                                            missions = missions,
                                            reviewItems = reviewItems,
                                            onClaimMission = { vm.claimMission(it) },
                                            onOpenAiCoach = { activeModal = "coach" },
                                            onOpenRevision = { activeModal = "revision" }
                                        )
                                    }
                                }

                                if (statusBanner != null) {
                                    LaunchedEffect(statusBanner) {
                                        kotlinx.coroutines.delay(3500L)
                                        vm.clearBanner()
                                    }
                                    Snackbar(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(16.dp),
                                        action = {
                                            TextButton(onClick = { vm.clearBanner() }) {
                                                Text("OK")
                                            }
                                        }
                                    ) {
                                        Text(statusBanner.orEmpty())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showNotifications) {
            NotificationsDialog(
                notifications = notifications,
                onDismiss = {
                    vm.markNotificationsRead()
                    showNotifications = false
                }
            )
        }
    }
}
