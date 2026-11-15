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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyLevel
import com.example.data.model.FriendChallenge
import com.example.data.model.Friendship
import com.example.data.model.GameMode
import com.example.data.model.QuizCatalogs
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.questions.QuestionBank
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.QuizConfig

@Composable
fun FriendsScreen(
    currentUserId: String,
    publicPlayers: List<UserProfile>,
    friendships: List<Friendship>,
    challenges: List<FriendChallenge>,
    blockedIds: Set<String>,
    onSendFriendRequest: (String) -> Unit,
    onAcceptFriendRequest: (String) -> Unit,
    onSendChallenge: (String, String, DifficultyLevel, Int) -> Unit,
    onAcceptAndPlayChallenge: (QuizConfig) -> Unit,
    onSendGift: (VirtualGift, String) -> Unit,
    onBlockPlayer: (String) -> Unit,
    onReportPlayer: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var challengeTarget by remember { mutableStateOf<UserProfile?>(null) }
    var giftTarget by remember { mutableStateOf<UserProfile?>(null) }
    var reportTarget by remember { mutableStateOf<UserProfile?>(null) }
    var selectedCommunity by remember { mutableStateOf("Sciences") }

    val acceptedFriendIds = friendships.filter { it.status == "accepted" }
        .flatMap { it.userIds }
        .filter { it != currentUserId && it !in blockedIds }
        .toSet()

    val incomingRequests = friendships.filter {
        it.status == "pending" && it.requesterId != currentUserId && currentUserId in it.userIds
    }

    val visiblePlayers = publicPlayers.filter {
        it.userId !in blockedIds &&
            (searchQuery.isBlank() || it.pseudo.contains(searchQuery, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Rechercher un ami ou joueur...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (incomingRequests.isNotEmpty()) {
            item {
                Text("Invitations reçues (${incomingRequests.size})", style = MaterialTheme.typography.titleLarge)
            }
            items(incomingRequests, key = { it.id }) { req ->
                val sender = publicPlayers.firstOrNull { it.userId == req.requesterId }
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(sender?.pseudo ?: "Joueur", style = MaterialTheme.typography.titleMedium)
                            Text("Souhaite vous ajouter en ami", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(onClick = { onAcceptFriendRequest(req.id) }) {
                            Text("Accepter")
                        }
                    }
                }
            }
        }

        if (challenges.isNotEmpty()) {
            item {
                Text("Défis & Revanches", style = MaterialTheme.typography.titleLarge)
            }
            items(challenges, key = { it.id }) { ch ->
                val isReceiver = ch.receiverId == currentUserId
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${ch.category} • ${ch.questionCount} questions", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = when (ch.status) {
                                    "completed" -> "Terminé : Envoyeur ${ch.senderScore} vs Receveur ${ch.receiverScore}"
                                    else -> if (isReceiver) "Défi reçu (${ch.difficulty})" else "En attente de réponse"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isReceiver && ch.status == "pending") {
                            Button(onClick = {
                                onAcceptAndPlayChallenge(
                                    QuizConfig(
                                        mode = GameMode.DUEL,
                                        category = ch.category,
                                        difficulty = DifficultyLevel.entries.firstOrNull { it.label == ch.difficulty } ?: DifficultyLevel.MEDIUM,
                                        questionCount = ch.questionCount.toInt(),
                                        timePerQuestionSec = 15,
                                        activeChallengeId = ch.id
                                    )
                                )
                            }) {
                                Text("Relever")
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Communautés par catégorie", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Sciences", "Culture sénégalaise", "Football", "Informatique", "Histoire", "Mathématiques").forEach { cat ->
                    FilterChip(
                        selected = selectedCommunity == cat,
                        onClick = { selectedCommunity = cat },
                        label = { Text(cat) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Communauté $selectedCommunity", style = MaterialTheme.typography.titleMedium)
                        Text("Entraînez-vous ensemble dans un espace modéré et bienveillant.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(onClick = {
                        onAcceptAndPlayChallenge(
                            QuizConfig(mode = GameMode.RANDOM, category = selectedCommunity, questionCount = 10, timePerQuestionSec = 15)
                        )
                    }) {
                        Text("Jouer")
                    }
                }
            }
        }

        item {
            Text(
                text = "Joueurs & Amis (${visiblePlayers.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (visiblePlayers.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Text(
                        text = "Aucun autre joueur trouvé pour le moment. Invitez vos amis ou recherchez un pseudo !",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(visiblePlayers, key = { it.userId }) { player ->
                val isFriend = player.userId in acceptedFriendIds
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
                                Text(QuizCatalogs.avatarEmojiFor(player.avatarId), fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(player.pseudo, style = MaterialTheme.typography.titleMedium)
                                        if (isFriend) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(color = EmeraldSuccess.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                                                Text("Ami", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                    Text(
                                        "Niv. ${player.level} • ${player.division}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Row {
                                if (!isFriend) {
                                    IconButton(onClick = { onSendFriendRequest(player.userId) }) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = "Ajouter ami", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                IconButton(onClick = { challengeTarget = player }) {
                                    Icon(Icons.Default.SportsEsports, contentDescription = "Défier", tint = GoldAccent)
                                }
                                IconButton(onClick = { giftTarget = player }) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = "Offrir cadeau", tint = MaterialTheme.colorScheme.secondary)
                                }
                                IconButton(onClick = { onBlockPlayer(player.userId) }) {
                                    Icon(Icons.Default.Block, contentDescription = "Bloquer")
                                }
                                IconButton(onClick = { reportTarget = player }) {
                                    Icon(Icons.Default.Flag, contentDescription = "Signaler")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Challenge Dialog
    challengeTarget?.let { target ->
        var chCat by remember { mutableStateOf("Sciences") }
        var chDiff by remember { mutableStateOf(DifficultyLevel.MEDIUM) }
        var chCount by remember { mutableIntStateOf(10) }
        AlertDialog(
            onDismissRequest = { challengeTarget = null },
            title = { Text("Défier ${target.pseudo}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Catégorie :", style = MaterialTheme.typography.labelLarge)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        QuestionBank.allCategories.take(10).forEach { c ->
                            FilterChip(selected = chCat == c, onClick = { chCat = c }, label = { Text(c) })
                        }
                    }
                    Text("Questions :", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(10, 15, 20).forEach { n ->
                            FilterChip(selected = chCount == n, onClick = { chCount = n }, label = { Text("$n") })
                        }
                    }
                    Text("Difficulté :", style = MaterialTheme.typography.labelLarge)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DifficultyLevel.entries.forEach { d ->
                            FilterChip(selected = chDiff == d, onClick = { chDiff = d }, label = { Text(d.label) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSendChallenge(target.userId, chCat, chDiff, chCount)
                    challengeTarget = null
                }) { Text("Envoyer le défi") }
            },
            dismissButton = {
                TextButton(onClick = { challengeTarget = null }) { Text("Annuler") }
            }
        )
    }

    // Virtual Gift Dialog
    giftTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { giftTarget = null },
            title = { Text("Cadeau décoratif pour ${target.pseudo}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Les cadeaux sont purement décoratifs et non échangeables en argent réel.", style = MaterialTheme.typography.bodyMedium)
                    QuizCatalogs.virtualGifts.forEach { gift ->
                        OutlinedButton(
                            onClick = {
                                onSendGift(gift, target.pseudo)
                                giftTarget = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${gift.emoji} ${gift.name} — ${gift.costCoins} QuizCoins")
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { giftTarget = null }) { Text("Fermer") } }
        )
    }

    // Report Dialog
    reportTarget?.let { target ->
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { reportTarget = null },
            title = { Text("Signaler ${target.pseudo}") },
            text = {
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Motif du signalement") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (reason.isNotBlank()) onReportPlayer(target.userId, reason)
                    reportTarget = null
                }) { Text("Envoyer") }
            },
            dismissButton = { TextButton(onClick = { reportTarget = null }) { Text("Annuler") } }
        )
    }
}
