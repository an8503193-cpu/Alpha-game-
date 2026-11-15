package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Psychology
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.Division
import com.example.data.model.MatchHistory
import com.example.data.model.MissionProgress
import com.example.data.model.QuizCatalogs
import com.example.data.model.ReviewItem
import com.example.data.model.UserProfile
import com.example.ui.components.AvatarBadge
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent

@Composable
fun ProfileScreen(
    profile: UserProfile?,
    history: List<MatchHistory>,
    missions: List<MissionProgress>,
    reviewItems: List<ReviewItem>,
    onClaimMission: (MissionProgress) -> Unit,
    onOpenAiCoach: () -> Unit,
    onOpenRevision: () -> Unit
) {
    var pickedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) pickedPhotoUri = uri
    }

    val p = profile ?: UserProfile()
    val div = Division.entries.firstOrNull { it.label == p.division } ?: Division.BRONZE
    val winRate = if (p.gamesPlayed > 0) ((p.wins * 100) / p.gamesPlayed).toInt() else 0
    val accuracy = if (p.totalAnswers > 0) ((p.correctAnswers * 100) / p.totalAnswers).toInt() else 0
    val badges = remember(p) { QuizCatalogs.computeBadges(p) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            if (pickedPhotoUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(pickedPhotoUri),
                                    contentDescription = "Photo de profil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                )
                            } else {
                                AvatarBadge(avatarId = p.avatarId, frameId = p.frameId, size = 68.dp)
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable {
                                        photoPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.PhotoLibrary,
                                        contentDescription = "Choisir une photo",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.pseudo, style = MaterialTheme.typography.headlineSmall)
                            Text(
                                text = "${div.badgeEmoji} ${div.label} • ${QuizCatalogs.titleLabelFor(p.titleId)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GoldAccent
                            )
                            Text(
                                text = "Niveau ${p.level} • ${p.xp} XP • ${p.quizCoins} 🪙",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = onOpenAiCoach,
                            modifier = Modifier.weight(1f).testTag("profile_ai_coach_btn"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Coach IA")
                        }
                        OutlinedButton(
                            onClick = onOpenRevision,
                            modifier = Modifier.weight(1f).testTag("profile_revision_btn"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Révision (${reviewItems.size})")
                        }
                    }
                }
            }
        }

        // 7-Day Daily Streak Calendar
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Calendrier de série quotidienne", style = MaterialTheme.typography.titleMedium)
                        Text("🔥 ${p.streakDays} jour(s)", style = MaterialTheme.typography.labelSmall, color = GoldAccent)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val activeDays = p.streakDays.coerceIn(1L, 7L).toInt()
                        for (day in 1..7) {
                            val reached = day <= activeDays
                            Surface(
                                color = if (reached) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("J$day", style = MaterialTheme.typography.labelSmall)
                                    Text(if (reached) "🔥" else "🪙", fontSize = 14.sp)
                                    Text("+${25 + day * 10}", style = MaterialTheme.typography.labelSmall, color = GoldAccent)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Smart Statistics & Category Performance Chart
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Statistiques intelligentes", style = MaterialTheme.typography.titleLarge)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatMetricBox("Parties", "${p.gamesPlayed}", Modifier.weight(1f))
                        StatMetricBox("Victoires", "${p.wins} ($winRate%)", Modifier.weight(1f))
                        StatMetricBox("Précision", "$accuracy%", Modifier.weight(1f))
                        StatMetricBox("Record", "${p.bestScore}", Modifier.weight(1f))
                    }

                    val catStats = remember(history) {
                        if (history.isEmpty()) {
                            listOf("Sciences" to 0f, "Histoire" to 0f, "Culture" to 0f, "Maths" to 0f, "Sport" to 0f)
                        } else {
                            history.groupBy { it.category }.entries.take(5).map { (cat, list) ->
                                val corr = list.sumOf { it.correctCount }.toFloat()
                                val tot = list.sumOf { it.totalCount }.coerceAtLeast(1L).toFloat()
                                cat.take(9) to (corr / tot).coerceIn(0.05f, 1f)
                            }
                        }
                    }
                    Text("Précision par catégorie jouée", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val primaryCol = MaterialTheme.colorScheme.primary
                    val secondaryCol = MaterialTheme.colorScheme.secondary
                    val trackCol = MaterialTheme.colorScheme.surfaceVariant
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    ) {
                        val barCount = catStats.size.coerceAtLeast(1)
                        val slotWidth = size.width / barCount
                        val barWidth = (slotWidth * 0.52f).coerceAtLeast(14.dp.toPx())
                        catStats.forEachIndexed { i, (_, ratio) ->
                            val x = i * slotWidth + (slotWidth - barWidth) / 2f
                            drawRoundRect(
                                color = trackCol,
                                topLeft = Offset(x, 0f),
                                size = Size(barWidth, size.height),
                                cornerRadius = CornerRadius(10.dp.toPx())
                            )
                            val fillHeight = size.height * ratio.coerceAtLeast(0.06f)
                            drawRoundRect(
                                color = if (i % 2 == 0) primaryCol else secondaryCol,
                                topLeft = Offset(x, size.height - fillHeight),
                                size = Size(barWidth, fillHeight),
                                cornerRadius = CornerRadius(10.dp.toPx())
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        catStats.forEach { (label, ratio) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${(ratio * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Daily & Weekly Missions
        item {
            Text("Missions actives", style = MaterialTheme.typography.titleLarge)
        }
        items(missions, key = { it.missionKey }) { mission ->
            val label = missionTitle(mission.missionKey)
            val ratio = (mission.progress.toFloat() / mission.target.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
            val ready = !mission.claimed && mission.progress >= mission.target
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(label, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${if (mission.period == "daily") "Quotidienne" else "Hebdomadaire"} • ${mission.progress}/${mission.target}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)),
                            color = if (mission.claimed) EmeraldSuccess else MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { onClaimMission(mission) },
                        enabled = ready,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            when {
                                mission.claimed -> "Reçu"
                                ready -> "Réclamer"
                                else -> "En cours"
                            }
                        )
                    }
                }
            }
        }

        // Badges Gallery
        item {
            Text("Galerie de Badges (${badges.count { it.unlocked }}/${badges.size})", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                badges.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        pair.forEach { b ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (b.unlocked) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(b.emoji, fontSize = 24.sp)
                                    Column {
                                        Text(b.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            text = if (b.unlocked) "Débloqué" else b.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (b.unlocked) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Match History
        item {
            Text("Historique des parties", style = MaterialTheme.typography.titleLarge)
        }
        if (history.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        "Aucune partie enregistrée pour l'instant. Lancez votre premier duel !",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(history, key = { it.id }) { m ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${m.mode} • ${m.category}", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Rang #${m.rank} • ${m.correctCount}/${m.totalCount} bonnes réponses",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${m.score} pts", style = MaterialTheme.typography.titleMedium, color = GoldAccent)
                            Text("+${m.xpEarned} XP", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMetricBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

fun missionTitle(key: String): String = when (key) {
    "win_3" -> "Gagner 3 parties"
    "answer_20" -> "Réussir 20 bonnes réponses"
    "categories_3" -> "Explorer 3 catégories"
    "streak_5" -> "Série de 5 jours de connexion"
    "challenge_friend" -> "Défier un ami"
    "play_tournament" -> "Participer à un tournoi"
    else -> key
}
