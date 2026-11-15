package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.DifficultyLevel
import com.example.data.model.GameMode
import com.example.data.model.MissionProgress
import com.example.data.model.ReviewItem
import com.example.data.questions.QuestionBank
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.AiCoachUiState
import com.example.ui.viewmodel.QuizConfig

@Composable
fun AiCoachScreen(
    aiCoachState: AiCoachUiState,
    onRunDiagnosis: () -> Unit,
    onGenerateExercise: (String, DifficultyLevel) -> Unit,
    onStartRecommendedTraining: (QuizConfig) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedCategory by remember { mutableStateOf("Sciences") }
    var selectedDiff by remember { mutableStateOf(DifficultyLevel.MEDIUM) }
    var answeredExerciseIdx by remember { mutableStateOf<Int?>(null) }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Coach IA Pédagogique", style = MaterialTheme.typography.headlineSmall)
            }

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ai_coach_avatar),
                        contentDescription = "Avatar Coach IA",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Assistant d'entraînement QuizFriend", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Analyse vos statistiques et génère des exercices sur mesure. Ne modifie jamais vos scores ni les classements officiels.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Button(
                onClick = onRunDiagnosis,
                modifier = Modifier.fillMaxWidth().height(52.dp).testTag("coach_diagnosis_btn"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Psychology, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyser mes performances")
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Générer un exercice IA", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuestionBank.allCategories.drop(1).take(12).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(DifficultyLevel.EASY, DifficultyLevel.MEDIUM, DifficultyLevel.HARD, DifficultyLevel.EXPERT).forEach { d ->
                            FilterChip(
                                selected = selectedDiff == d,
                                onClick = { selectedDiff = d },
                                label = { Text(d.label) }
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            answeredExerciseIdx = null
                            onGenerateExercise(selectedCategory, selectedDiff)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("coach_generate_exercise_btn"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Créer une question d'entraînement")
                    }
                }
            }

            when (aiCoachState) {
                is AiCoachUiState.Idle -> Unit
                is AiCoachUiState.Loading -> Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                is AiCoachUiState.Error -> Card(
                    colors = CardDefaults.cardColors(containerColor = CrimsonAlert.copy(alpha = 0.16f))
                ) {
                    Text(
                        aiCoachState.message,
                        color = CrimsonAlert,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is AiCoachUiState.StreamingText -> Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(aiCoachState.text, modifier = Modifier.padding(16.dp))
                }
                is AiCoachUiState.DiagnosisReady -> {
                    val d = aiCoachState.diagnosis
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Bilan personnalisé", style = MaterialTheme.typography.titleLarge, color = GoldAccent)
                            Text(d.summary, style = MaterialTheme.typography.bodyLarge)
                            Text("Points forts : ${d.strongCategories.joinToString(", ")}", color = EmeraldSuccess)
                            Text("À renforcer : ${d.weakCategories.joinToString(", ")}", color = MaterialTheme.colorScheme.secondary)
                            Text("Conseil : ${d.studyTip}", style = MaterialTheme.typography.bodyMedium)
                            Button(
                                onClick = {
                                    onStartRecommendedTraining(
                                        QuizConfig(
                                            mode = GameMode.TRAINING,
                                            category = d.recommendedCategory,
                                            difficulty = DifficultyLevel.MIXED,
                                            questionCount = 10,
                                            timePerQuestionSec = 0
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("S'entraîner sur : ${d.recommendedCategory}")
                            }
                        }
                    }
                }
                is AiCoachUiState.TrainingQuestionReady -> {
                    val q = aiCoachState.question
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    "Exercice généré par IA • Hors classement",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(q.question, style = MaterialTheme.typography.titleMedium)
                            q.options.forEachIndexed { idx, opt ->
                                val picked = answeredExerciseIdx
                                val bg = when {
                                    picked == null -> MaterialTheme.colorScheme.surfaceVariant
                                    idx == q.correctIndex -> EmeraldSuccess.copy(alpha = 0.25f)
                                    idx == picked -> CrimsonAlert.copy(alpha = 0.25f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                                Surface(
                                    color = bg,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable(enabled = picked == null) { answeredExerciseIdx = idx }
                                ) {
                                    Text(opt, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Medium)
                                }
                            }
                            if (answeredExerciseIdx != null) {
                                Text(q.explanation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RevisionScreen(
    reviewItems: List<ReviewItem>,
    onStartRevisionSession: (List<ReviewItem>) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Mode Révision", style = MaterialTheme.typography.headlineSmall)
            }

            Button(
                onClick = { onStartRevisionSession(reviewItems) },
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("start_revision_quiz_btn"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lancer une session révision sans chrono")
            }

            if (reviewItems.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Aucune erreur ou favori pour le moment", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Vos questions ratées et vos favoris (🔖) apparaîtront ici automatiquement pour des révisions ciblées.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(reviewItems, key = { it.questionId }) { item ->
                        val q = QuestionBank.findById(item.questionId)
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    Text(
                                        "${if (item.isFavorite) "🔖 Favori • " else ""}${item.missedCount} erreur(s)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldAccent
                                    )
                                }
                                if (q != null) {
                                    Text(q.question, style = MaterialTheme.typography.titleMedium)
                                    Text("Réponse : ${q.options[q.correctIndex]}", color = EmeraldSuccess, style = MaterialTheme.typography.labelLarge)
                                    Text(q.explanation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    Text("Question #${item.questionId}", style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MissionsOverlayScreen(
    missions: List<MissionProgress>,
    onClaimMission: (MissionProgress) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Missions quotidiennes & hebdomadaires", style = MaterialTheme.typography.titleLarge)
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(missions, key = { it.missionKey }) { m ->
                    val ready = !m.claimed && m.progress >= m.target
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(missionTitle(m.missionKey), style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${if (m.period == "daily") "Quotidienne" else "Hebdomadaire"} • ${m.progress}/${m.target} • +120 XP & +75 🪙",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(onClick = { onClaimMission(m) }, enabled = ready) {
                                Text(if (m.claimed) "Reçu" else if (ready) "Réclamer" else "${m.progress}/${m.target}")
                            }
                        }
                    }
                }
            }
        }
    }
}
