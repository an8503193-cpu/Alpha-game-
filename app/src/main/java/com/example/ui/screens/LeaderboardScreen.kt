package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Division
import com.example.data.model.GameMode
import com.example.data.model.LeaderboardEntry
import com.example.data.model.QuizCatalogs
import com.example.data.model.UserProfile
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.QuizConfig

@Composable
fun LeaderboardScreen(
    profile: UserProfile?,
    entries: List<LeaderboardEntry>,
    friendIds: Set<String>,
    onJoinTournament: (QuizConfig) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var scopeFilter by remember { mutableStateOf("Mondial") }
    var limitTop by remember { mutableIntStateOf(50) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Classements") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Tournois") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Pass Saison") })
        }

        when (selectedTab) {
            0 -> {
                val filtered = entries.filter { entry ->
                    when (scopeFilter) {
                        "Par pays" -> entry.country == (profile?.country ?: "SN")
                        "Amis" -> entry.userId == profile?.userId || entry.userId in friendIds
                        else -> true
                    }
                }.let { list ->
                    when (scopeFilter) {
                        "Hebdo" -> list.sortedByDescending { it.weeklyPoints }
                        "Mensuel" -> list.sortedByDescending { it.monthlyPoints }
                        else -> list.sortedByDescending { it.seasonPoints }
                    }
                }.take(limitTop)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        // Divisions banner
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Division.entries.forEach { div ->
                                val isCurrent = profile?.division == div.label
                                Surface(
                                    color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(div.badgeEmoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(div.label, style = MaterialTheme.typography.labelLarge)
                                            Text("${div.minPoints}+ pts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Mondial", "Par pays", "Amis", "Hebdo", "Mensuel").forEach { f ->
                                FilterChip(selected = scopeFilter == f, onClick = { scopeFilter = f }, label = { Text(f) })
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(10, 50, 100).forEach { top ->
                                FilterChip(selected = limitTop == top, onClick = { limitTop = top }, label = { Text("Top $top") })
                            }
                        }
                    }

                    if (filtered.isEmpty()) {
                        item {
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Text(
                                    text = "Aucun joueur classé dans ce filtre pour le moment. Jouez une partie classée pour apparaître ! (Les robots IA ne figurent jamais dans le classement humain.)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        itemsIndexed(filtered, key = { _, item -> item.userId }) { idx, entry ->
                            val isMe = entry.userId == profile?.userId
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isMe) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "#${idx + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (idx < 3) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(QuizCatalogs.avatarEmojiFor(entry.avatarId), fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(entry.pseudo, style = MaterialTheme.typography.titleMedium)
                                            Text(
                                                "${entry.division} • ${entry.country} • ${entry.wins} victoires",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    val pts = when (scopeFilter) {
                                        "Hebdo" -> entry.weeklyPoints
                                        "Mensuel" -> entry.monthlyPoints
                                        else -> entry.seasonPoints
                                    }
                                    Text("$pts pts", style = MaterialTheme.typography.titleMedium, color = GoldAccent, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuizCatalogs.tournaments.forEach { t ->
                        item(key = t.id) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("🏆 ${t.trophyName}", style = MaterialTheme.typography.labelLarge, color = GoldAccent)
                                        Text("+${t.rewardCoins} QuizCoins", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess)
                                    }
                                    Text(t.title, style = MaterialTheme.typography.titleLarge)
                                    Text("${t.stage} • Catégorie : ${t.category}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (t.isSponsored && t.sponsorName != null) {
                                        Text("Sponsorisé par ${t.sponsorName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(
                                        onClick = {
                                            onJoinTournament(
                                                QuizConfig(
                                                    mode = GameMode.DUEL,
                                                    category = t.category,
                                                    questionCount = 15,
                                                    timePerQuestionSec = 15
                                                )
                                            )
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Participer aux qualifications")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                val playerXp = profile?.xp ?: 0L
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Pass de Saison 1 — Gratuit pour tous", style = MaterialTheme.typography.titleLarge)
                                Text(
                                    "Votre XP totale : $playerXp XP. Aucune récompense essentielle au jeu n'est payante.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    QuizCatalogs.seasonPassTiers.forEach { tier ->
                        val unlocked = playerXp >= tier.xpRequired
                        val progress = (playerXp.toFloat() / tier.xpRequired.toFloat()).coerceIn(0f, 1f)
                        item(key = tier.tier) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(tier.rewardEmoji, fontSize = 26.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("Palier ${tier.tier} • ${tier.rewardLabel}", style = MaterialTheme.typography.titleMedium)
                                                Text("${tier.xpRequired} XP requis", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        Surface(
                                            color = if (unlocked) EmeraldSuccess.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = if (unlocked) "Débloqué" else "À débloquer",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (unlocked) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
