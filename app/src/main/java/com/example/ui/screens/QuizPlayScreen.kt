package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.QuizQuestion
import com.example.ui.components.QuizReactionsStrip
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.ActiveQuizSession
import com.example.ui.viewmodel.AiCoachUiState

@Composable
fun QuizPlayScreen(
    session: ActiveQuizSession,
    reactionsEnabled: Boolean,
    aiCoachState: AiCoachUiState,
    onSelectOption: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onSendReaction: (String) -> Unit,
    onToggleFavorite: (QuizQuestion, Boolean) -> Unit,
    onAskAiExplanation: (QuizQuestion, String?) -> Unit,
    onExitQuiz: () -> Unit
) {
    BackHandler { onExitQuiz() }

    val question = session.questions[session.currentIndex]
    var isFav by remember(question.id) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top HUD (Back, Question Index, Timer, Score)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onExitQuiz) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quitter")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${session.config.mode.title} • ${session.currentIndex + 1}/${session.questions.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${question.category} • ${question.difficulty.label}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "${session.score} pts",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldAccent,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Timer bar (Zero ads during live quiz or timer!)
            if (session.config.timePerQuestionSec > 0) {
                val progress = session.remainingSec.toFloat() / session.config.timePerQuestionSec.toFloat()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Chronomètre",
                        tint = if (session.remainingSec <= 4) CrimsonAlert else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = if (session.remainingSec <= 4) CrimsonAlert else MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${session.remainingSec}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (session.remainingSec <= 4) CrimsonAlert else MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Bot opponents strip (clearly labeled [IA])
            if (session.botScores.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    session.botScores.forEach { (name, botScore) ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(name, style = MaterialTheme.typography.labelMedium, maxLines = 1)
                                Text("$botScore pts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }

            // Question Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (question.isAiGenerated) {
                            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    "Générée par IA • Entraînement",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "Source : ${question.source}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = {
                            isFav = !isFav
                            onToggleFavorite(question, isFav)
                        }) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Favori",
                                tint = if (isFav) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // 4 Answer Options
            question.options.forEachIndexed { idx, optionText ->
                val isSelected = session.selectedOptionIndex == idx
                val isCorrectOption = idx == question.correctIndex
                val bgColor: Color
                val borderColor: Color
                when {
                    !session.answerRevealed -> {
                        bgColor = MaterialTheme.colorScheme.surface
                        borderColor = MaterialTheme.colorScheme.surfaceVariant
                    }
                    isCorrectOption -> {
                        bgColor = EmeraldSuccess.copy(alpha = 0.22f)
                        borderColor = EmeraldSuccess
                    }
                    isSelected -> {
                        bgColor = CrimsonAlert.copy(alpha = 0.22f)
                        borderColor = CrimsonAlert
                    }
                    else -> {
                        bgColor = MaterialTheme.colorScheme.surface
                        borderColor = MaterialTheme.colorScheme.surfaceVariant
                    }
                }
                val letter = listOf("A", "B", "C", "D")[idx]
                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !session.answerRevealed && !session.config.isSpectator) {
                            onSelectOption(idx)
                        }
                        .testTag("quiz_option_$idx")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(letter, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // QuizReactions strip (never blocks timer or questions)
            QuizReactionsStrip(
                enabled = reactionsEnabled,
                bubbles = session.reactions,
                reactionCounts = session.reactionCounts,
                onSendReaction = onSendReaction
            )

            // Explanation + Next button after reveal
            AnimatedVisibility(visible = session.answerRevealed) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val wasRight = session.selectedOptionIndex == question.correctIndex
                        Text(
                            text = if (wasRight) "Bonne réponse ! (+${session.records.lastOrNull()?.pointsEarned ?: 0} pts)"
                            else "Réponse correcte : ${question.options[question.correctIndex]}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (wasRight) EmeraldSuccess else GoldAccent
                        )
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (!wasRight) {
                            OutlinedButton(
                                onClick = {
                                    val chosen = session.selectedOptionIndex?.let { question.options.getOrNull(it) }
                                    onAskAiExplanation(question, chosen)
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Approfondir avec le Coach IA")
                            }
                            when (aiCoachState) {
                                is AiCoachUiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(22.dp))
                                is AiCoachUiState.StreamingText -> Text(
                                    text = aiCoachState.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                is AiCoachUiState.Error -> Text(
                                    text = aiCoachState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                                else -> Unit
                            }
                        }

                        Button(
                            onClick = onNextQuestion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("next_question_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                if (session.currentIndex + 1 < session.questions.size) "Question suivante"
                                else "Voir les résultats"
                            )
                        }
                    }
                }
            }
        }
    }
}
