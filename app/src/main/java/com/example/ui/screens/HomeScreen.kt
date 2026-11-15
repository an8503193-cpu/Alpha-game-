package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Division
import com.example.data.model.FriendChallenge
import com.example.data.model.GameMode
import com.example.data.model.MissionProgress
import com.example.data.model.QuizCatalogs
import com.example.data.model.UserProfile
import com.example.data.questions.QuestionBank
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.QuizConfig

@Composable
fun HomeScreen(
    profile: UserProfile?,
    missions: List<MissionProgress>,
    challenges: List<FriendChallenge>,
    onNavigateTab: (Int) -> Unit,
    onQuickStartQuiz: (QuizConfig) -> Unit,
    onOpenAiCoach: () -> Unit,
    onOpenRevision: () -> Unit,
    onOpenMissions: () -> Unit
) {
    val pendingChallenges = challenges.filter { it.status == "pending" && it.receiverId == profile?.userId }
    val readyMissions = missions.count { !it.claimed && it.progress >= it.target }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner + Slogan + Daily Quiz CTA
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(185.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_quizfriend),
                        contentDescription = "QuizFriend Arène",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.50f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${QuestionBank.allQuestions.size}+ QUESTIONS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🔥 Série ${profile?.streakDays ?: 1}j",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GoldAccent
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "QuizFriend",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Défie tes amis. Teste ton intelligence. Deviens une légende !",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onNavigateTab(1) },
                                modifier = Modifier.testTag("home_play_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Jouer")
                            }
                            OutlinedButton(
                                onClick = {
                                    onQuickStartQuiz(
                                        QuizConfig(
                                            mode = GameMode.DAILY_QUIZ,
                                            category = "Toutes",
                                            questionCount = 10,
                                            timePerQuestionSec = 15
                                        )
                                    )
                                },
                                modifier = Modifier.testTag("home_daily_quiz_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quiz du Jour")
                            }
                        }
                    }
                }
            }
        }

        // Player Division & XP Progress Card
        item {
            val div = Division.entries.firstOrNull { it.label == profile?.division } ?: Division.BRONZE
            val level = profile?.level ?: 1L
            val xpInLevel = (profile?.xp ?: 0L) % 250L
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${div.badgeEmoji} Division ${div.label}",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldAccent
                            )
                            Text(
                                text = QuizCatalogs.titleLabelFor(profile?.titleId ?: "title_rookie"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Niveau $level",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "$xpInLevel / 250 XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { xpInLevel.toFloat() / 250f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Quick Action Grid (Multijoueur, Entraînement, Coach IA, Révision, Missions, Tournois, Amis, Boutique)
        item {
            Text(
                text = "Accès rapide",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    HomeActionTile(
                        title = "Multijoueur",
                        subtitle = "Duel, Trio, Équipe",
                        icon = Icons.Default.Groups,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(1) }
                    )
                    HomeActionTile(
                        title = "Entraînement",
                        subtitle = "Solo sans chrono",
                        icon = Icons.Default.School,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onQuickStartQuiz(
                                QuizConfig(
                                    mode = GameMode.TRAINING,
                                    category = "Toutes",
                                    questionCount = 10,
                                    timePerQuestionSec = 0
                                )
                            )
                        }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    HomeActionTile(
                        title = "Coach IA",
                        subtitle = "Analyse & conseils",
                        icon = Icons.Default.Psychology,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenAiCoach
                    )
                    HomeActionTile(
                        title = "Mode Révision",
                        subtitle = "Questions ratées",
                        icon = Icons.Default.MenuBook,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenRevision
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    HomeActionTile(
                        title = if (readyMissions > 0) "Missions ($readyMissions)" else "Missions",
                        subtitle = "XP & QuizCoins",
                        icon = Icons.Default.Flag,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenMissions
                    )
                    HomeActionTile(
                        title = "Tournois",
                        subtitle = "Pass & Divisions",
                        icon = Icons.Default.EmojiEvents,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(2) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    HomeActionTile(
                        title = "Amis & Défis",
                        subtitle = "${pendingChallenges.size} défi(s) reçu(s)",
                        icon = Icons.Default.AutoAwesome,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(3) }
                    )
                    HomeActionTile(
                        title = "Boutique",
                        subtitle = "Avatars & Thèmes",
                        icon = Icons.Default.Storefront,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(4) }
                    )
                }
            }
        }

        // Tournaments & Events Carousel
        item {
            Text(
                text = "Événements & Tournois ouverts",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuizCatalogs.tournaments.forEach { tournament ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.width(245.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🏆 ${tournament.trophyName}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GoldAccent
                                )
                                Text(
                                    text = "+${tournament.rewardCoins} 🪙",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldSuccess
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = tournament.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = tournament.stage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    onQuickStartQuiz(
                                        QuizConfig(
                                            mode = GameMode.DUEL,
                                            category = tournament.category,
                                            questionCount = 15,
                                            timePerQuestionSec = 15
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Participer")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
