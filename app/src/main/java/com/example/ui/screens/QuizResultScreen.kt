package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.QuizCatalogs
import com.example.data.model.UserProfile
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.ActiveQuizSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QuizResultScreen(
    session: ActiveQuizSession,
    profile: UserProfile?,
    animationsEnabled: Boolean,
    onRematch: () -> Unit,
    onReturnHome: () -> Unit
) {
    BackHandler { onReturnHome() }

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var showShareSheet by remember { mutableStateOf(false) }
    var shareConsentGiven by remember { mutableStateOf(false) }
    var bannerDismissed by remember { mutableStateOf(false) }

    val myRank = (session.finalRankings.indexOfFirst { it.isCurrentUser } + 1).coerceAtLeast(1)
    val isVictory = myRank == 1 && session.correctCount > 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isVictory && animationsEnabled) {
            ConfettiOverlay(modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Victory or Encouraging Defeat Header
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isVictory) {
                        Image(
                            painter = painterResource(id = R.drawable.img_trophy_victory),
                            contentDescription = "Trophée de victoire",
                            modifier = Modifier
                                .size(112.dp)
                                .clip(RoundedCornerShape(22.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Victoire Épique ! 🏆", style = MaterialTheme.typography.headlineMedium, color = GoldAccent)
                        Text(
                            "Félicitations ${profile?.pseudo ?: ""}, vous dominez l'arène !",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text("💪", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Belle progression !",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            "Chaque question renforce vos connaissances. Continuez sur cette lancée !",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ResultStatPill("Score", "${session.score} pts", GoldAccent)
                        ResultStatPill("Bonnes rép.", "${session.correctCount}/${session.questions.size}", EmeraldSuccess)
                        ResultStatPill("XP gagnés", "+${session.xpEarned}", MaterialTheme.colorScheme.primary)
                        ResultStatPill("QuizCoins", "+${session.coinsEarned} 🪙", GoldAccent)
                    }
                }
            }

            // Podium / Final Rankings
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Classement de la partie", style = MaterialTheme.typography.titleLarge)
                    session.finalRankings.forEachIndexed { idx, p ->
                        Surface(
                            color = if (p.isCurrentUser) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("#${idx + 1}", style = MaterialTheme.typography.titleMedium, color = GoldAccent)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(p.avatarEmoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                                        if (p.teamName != null) {
                                            Text(p.teamName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
                                }
                                Text("${p.score} pts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Action Buttons (Share 9:16, Rematch, Home)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showShareSheet = true },
                    modifier = Modifier.weight(1f).testTag("share_result_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Partager 9:16")
                }
                Button(
                    onClick = onRematch,
                    modifier = Modifier.weight(1f).testTag("rematch_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Revanche")
                }
            }
            Button(
                onClick = onReturnHome,
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("result_home_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retour à l'accueil")
            }

            // Compliant post-game AdMob test slot (never during live quiz!)
            if (session.showPostGameAdBanner && !bannerDismissed) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Emplacement AdMob (Mode Test uniquement)", style = MaterialTheme.typography.labelSmall, color = GoldAccent)
                            Text(
                                "Bannière post-partie respectant les mineurs et la fréquence limitée. Configurez un ID AdMob réel avant publication.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        TextButton(onClick = { bannerDismissed = true }) {
                            Text("Fermer")
                        }
                    }
                }
            }

            // Answer Breakdown & Explanations
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Résumé des réponses & Explications", style = MaterialTheme.typography.titleLarge)
                    session.records.forEachIndexed { idx, rec ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${idx + 1}. ${rec.question.question}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (rec.isCorrect) EmeraldSuccess else CrimsonAlert
                                )
                                Text(
                                    text = "Bonne réponse : ${rec.question.options[rec.question.correctIndex]}",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = rec.question.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 9:16 Vertical Share Card Modal with Consent
    if (showShareSheet) {
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
        val shareText = "🏆 QuizFriend — ${profile?.pseudo ?: "Joueur"} : ${session.score} pts (Rang #$myRank, ${session.correctCount}/${session.questions.size} bonnes réponses) le $dateStr ! Défie-moi sur QuizFriend !"
        AlertDialog(
            onDismissRequest = { showShareSheet = false },
            title = { Text("Carte de résultat 9:16") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // 9:16 Vertical Preview Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .aspectRatio(9f / 16f)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⚡ QUIZFRIEND", style = MaterialTheme.typography.labelSmall, color = GoldAccent)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    QuizCatalogs.avatarEmojiFor(profile?.avatarId ?: "avatar_classic_spark"),
                                    fontSize = 42.sp
                                )
                                Text(profile?.pseudo ?: "Joueur", style = MaterialTheme.typography.titleMedium)
                                Text("Rang #$myRank", style = MaterialTheme.typography.labelMedium, color = EmeraldSuccess)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${session.score} PTS", style = MaterialTheme.typography.headlineMedium, color = GoldAccent)
                                Text("${session.correctCount}/${session.questions.size} correctes", style = MaterialTheme.typography.bodyMedium)
                                Text(session.config.mode.title, style = MaterialTheme.typography.labelMedium)
                            }
                            Text(dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (!shareConsentGiven) {
                        Text(
                            "Consentement : acceptez-vous de publier votre pseudo et votre score sur vos applications externes (WhatsApp, Instagram, Snapchat, Facebook, TikTok) ?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = { shareConsentGiven = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("J'accepte de partager ces infos")
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { clipboard.setText(AnnotatedString(shareText)) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Copier")
                            }
                            Button(
                                onClick = {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Partager via"))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Partager")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showShareSheet = false }) { Text("Fermer") }
            }
        )
    }
}

@Composable
private fun ResultStatPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ConfettiOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "confetti")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart),
        label = "confetti_y"
    )
    val palette = listOf(GoldAccent, EmeraldSuccess, Color(0xFF00E5FF), Color(0xFFD946EF), Color(0xFFFF5C77))
    Canvas(modifier = modifier) {
        for (i in 0..24) {
            val x = ((i * 73) % 100) / 100f * size.width
            val y = ((progress + i * 0.13f) % 1f) * size.height
            drawCircle(
                color = palette[i % palette.size].copy(alpha = 0.75f),
                radius = 5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}
