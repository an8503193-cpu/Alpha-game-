package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BotDifficulty
import com.example.data.model.DifficultyLevel
import com.example.data.model.GameMode
import com.example.data.model.GameRoom
import com.example.data.model.QuizCatalogs
import com.example.data.questions.QuestionBank
import com.example.ui.components.QuizReactionsStrip
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.QuizConfig

@Composable
fun PlayHubScreen(
    currentUserId: String,
    activeRoom: GameRoom?,
    openRooms: List<GameRoom>,
    onStartQuiz: (QuizConfig) -> Unit,
    onCreatePrivateRoom: (QuizConfig, Int) -> Unit,
    onJoinRoomByCode: (String) -> Unit,
    onAttachRoom: (String) -> Unit,
    onToggleReady: () -> Unit,
    onStartRoomMatch: () -> Unit,
    onSendRoomMessage: (String) -> Unit,
    onLeaveRoom: () -> Unit
) {
    var selectedMode by remember { mutableStateOf(GameMode.DUEL) }
    var selectedCategory by remember { mutableStateOf("Toutes") }
    var selectedDifficulty by remember { mutableStateOf(DifficultyLevel.MIXED) }
    var selectedCount by remember { mutableIntStateOf(10) }
    var customCountText by remember { mutableStateOf("") }
    var selectedTime by remember { mutableIntStateOf(15) }
    var selectedBot by remember { mutableStateOf(BotDifficulty.AMATEUR) }
    var privateMaxPlayers by remember { mutableIntStateOf(2) }
    var joinCodeInput by remember { mutableStateOf("") }
    var chatInput by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current

    if (activeRoom != null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Salon Privé • ${activeRoom.mode}", style = MaterialTheme.typography.titleLarge)
                                Text(
                                    text = "${activeRoom.category} • ${activeRoom.questionCount} questions • ${activeRoom.timePerQuestionSec}s",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable {
                                    clipboard.setText(AnnotatedString(activeRoom.roomCode))
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = activeRoom.roomCode,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copier code", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Participants (${activeRoom.participantIds.size}/${activeRoom.maxPlayers})",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        activeRoom.participantIds.forEach { uid ->
                            val pData = activeRoom.participants[uid]
                            val pseudo = pData?.get("pseudo") as? String ?: "Joueur"
                            val avatarId = pData?.get("avatarId") as? String ?: "avatar_classic_spark"
                            val isReady = pData?.get("ready") as? Boolean ?: false
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(QuizCatalogs.avatarEmojiFor(avatarId), fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(pseudo, style = MaterialTheme.typography.titleMedium)
                                            if (uid == activeRoom.hostId) {
                                                Text("Hôte du salon", style = MaterialTheme.typography.labelSmall, color = GoldAccent)
                                            }
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (isReady) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isReady) "Prêt" else "En attente",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isReady) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = onToggleReady,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Changer statut Prêt")
                            }
                            if (activeRoom.hostId == currentUserId) {
                                Button(
                                    onClick = onStartRoomMatch,
                                    modifier = Modifier.weight(1f).testTag("start_room_button"),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Démarrer")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onLeaveRoom,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Quitter la salle d'attente")
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Messages du salon", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (activeRoom.messages.isEmpty()) {
                            Text(
                                "Aucun message. Dites bonjour à vos adversaires !",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            activeRoom.messages.forEach { msg ->
                                Text(
                                    text = "${msg["sender"] ?: "Joueur"} : ${msg["text"] ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Message modéré...") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    onSendRoomMessage(chatInput)
                                    chatInput = ""
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Envoyer")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        QuizReactionsStrip(
                            enabled = true,
                            bubbles = emptyList(),
                            reactionCounts = emptyMap(),
                            onSendReaction = { emoji -> onSendRoomMessage(emoji) }
                        )
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Game Modes
        item {
            Text("Choisir un mode de jeu", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GameMode.entries.forEach { mode ->
                    val selected = mode == selectedMode
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .width(195.dp)
                            .clickable {
                                selectedMode = mode
                                if (mode == GameMode.TRAINING) selectedTime = 0
                                else if (selectedTime == 0) selectedTime = if (mode == GameMode.QUICK_ANSWER) 10 else 15
                            }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = mode.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mode.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (mode.isRanked) "Classé" else "Non classé",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (mode.isRanked) GoldAccent else EmeraldSuccess
                            )
                        }
                    }
                }
            }
        }

        // 2. Quiz Settings (Questions count, Timer, Difficulty, Category)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Paramètres de la partie", style = MaterialTheme.typography.titleLarge)

                    Text("Nombre de questions", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10, 15, 20, 30, 50, 100).forEach { cnt ->
                            FilterChip(
                                selected = selectedCount == cnt && customCountText.isBlank(),
                                onClick = {
                                    selectedCount = cnt
                                    customCountText = ""
                                },
                                label = { Text("$cnt") }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = customCountText,
                        onValueChange = { raw ->
                            customCountText = raw.filter { it.isDigit() }.take(3)
                            customCountText.toIntOrNull()?.let { n ->
                                if (n in 1..150) selectedCount = n
                            }
                        },
                        label = { Text("Nombre personnalisé (ex: 25)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Chronomètre par question", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val timeOptions = if (selectedMode == GameMode.TRAINING) listOf(10, 15, 20, 30, 0) else listOf(10, 15, 20, 30)
                        timeOptions.forEach { sec ->
                            FilterChip(
                                selected = selectedTime == sec,
                                onClick = { selectedTime = sec },
                                label = { Text(if (sec == 0) "Sans chrono" else "${sec}s") }
                            )
                        }
                    }

                    Text("Difficulté", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyLevel.entries.forEach { diff ->
                            FilterChip(
                                selected = selectedDifficulty == diff,
                                onClick = { selectedDifficulty = diff },
                                label = { Text(diff.label) }
                            )
                        }
                    }

                    Text("Catégorie (32 thèmes disponibles)", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuestionBank.allCategories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    if (selectedMode in listOf(GameMode.DUEL, GameMode.TRIO, GameMode.RANDOM, GameMode.TEAM)) {
                        Text("Robot IA adversaire (clairement signalé)", style = MaterialTheme.typography.titleMedium)
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BotDifficulty.entries.forEach { bot ->
                                FilterChip(
                                    selected = selectedBot == bot,
                                    onClick = { selectedBot = bot },
                                    leadingIcon = { Icon(Icons.Default.SmartToy, contentDescription = null) },
                                    label = { Text("${bot.label} (${(bot.accuracyRate * 100).toInt()}%)") }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            onStartQuiz(
                                QuizConfig(
                                    mode = selectedMode,
                                    category = selectedCategory,
                                    difficulty = selectedDifficulty,
                                    questionCount = selectedCount,
                                    timePerQuestionSec = selectedTime,
                                    botDifficulty = selectedBot,
                                    isSpectator = selectedMode == GameMode.SPECTATOR
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("launch_quiz_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lancer la partie (${selectedMode.title})")
                    }
                }
            }
        }

        // 3. Private Rooms (2 or 3 players, 6-char code) + Open Rooms / Spectator
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Salons Privés Multijoueurs", style = MaterialTheme.typography.titleLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = privateMaxPlayers == 2,
                            onClick = { privateMaxPlayers = 2 },
                            label = { Text("2 joueurs (Duel)") }
                        )
                        FilterChip(
                            selected = privateMaxPlayers == 3,
                            onClick = { privateMaxPlayers = 3 },
                            label = { Text("3 joueurs (Trio)") }
                        )
                    }
                    Button(
                        onClick = {
                            onCreatePrivateRoom(
                                QuizConfig(
                                    mode = if (privateMaxPlayers == 3) GameMode.TRIO else GameMode.DUEL,
                                    category = selectedCategory,
                                    difficulty = selectedDifficulty,
                                    questionCount = selectedCount,
                                    timePerQuestionSec = selectedTime
                                ),
                                privateMaxPlayers
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("create_private_room_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Créer un salon privé ($privateMaxPlayers joueurs)")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = joinCodeInput,
                            onValueChange = { joinCodeInput = it.uppercase().take(6) },
                            label = { Text("Code à 6 caractères") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { onJoinRoomByCode(joinCodeInput) },
                            modifier = Modifier.height(54.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Rejoindre")
                        }
                    }

                    if (openRooms.isNotEmpty()) {
                        Text("Salons ouverts / Mode Spectateur", style = MaterialTheme.typography.titleMedium)
                        openRooms.take(5).forEach { room ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Group, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("${room.mode} • ${room.roomCode}", style = MaterialTheme.typography.titleMedium)
                                            Text(
                                                "${room.category} (${room.participantIds.size}/${room.maxPlayers})",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        IconButton(onClick = { onAttachRoom(room.id) }) {
                                            Icon(Icons.Default.Visibility, contentDescription = "Observer")
                                        }
                                        OutlinedButton(onClick = { onJoinRoomByCode(room.roomCode) }) {
                                            Text("Rejoindre")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
