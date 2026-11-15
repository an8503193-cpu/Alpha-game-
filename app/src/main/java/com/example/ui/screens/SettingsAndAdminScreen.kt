package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppNotification
import com.example.data.model.QuizThemeStyle
import com.example.data.model.UserProfile
import com.example.data.prefs.AppPreferences
import com.example.data.questions.QuestionBank
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent

@Composable
fun NotificationsDialog(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notifications") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                notifications.forEach { n ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(n.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(n.timestampLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(n.body, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tout marquer comme lu") }
        }
    )
}

@Composable
fun SettingsScreen(
    profile: UserProfile?,
    preferences: AppPreferences,
    isAdminUser: Boolean,
    blockedCount: Int,
    mutedCount: Int,
    onUpdatePseudo: (String) -> Unit,
    onApplyTheme: (String) -> Unit,
    onUpdateAudio: (Boolean?, Boolean?, Float?, Float?, String?) -> Unit,
    onUpdatePrivacy: (Boolean?, Boolean?, Boolean?, Boolean?, Boolean?) -> Unit,
    onUpdateAppSettings: (Boolean?, Boolean?, Boolean?) -> Unit,
    onGrantTestRewardCoins: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var pseudoInput by remember(profile?.pseudo) { mutableStateOf(profile?.pseudo ?: "") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Paramètres", style = MaterialTheme.typography.headlineSmall)
            }

            // 1. Compte
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Compte", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = pseudoInput,
                        onValueChange = { pseudoInput = it },
                        label = { Text("Pseudo public (2–24 caractères)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("settings_pseudo_input")
                    )
                    Button(
                        onClick = { onUpdatePseudo(pseudoInput) },
                        modifier = Modifier.fillMaxWidth().testTag("settings_save_pseudo_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Enregistrer le pseudo")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = onSignOut,
                            modifier = Modifier.weight(1f).testTag("settings_signout_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Déconnexion")
                        }
                        OutlinedButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.weight(1f).testTag("settings_delete_account_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Supprimer")
                        }
                    }
                }
            }

            // 2. Apparence
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Apparence", style = MaterialTheme.typography.titleLarge)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuizThemeStyle.entries.forEach { style ->
                            FilterChip(
                                selected = preferences.themeId == style.id,
                                onClick = { onApplyTheme(style.id) },
                                label = { Text(style.label) }
                            )
                        }
                    }
                    SettingsSwitchRow(
                        title = "Animations & confettis",
                        subtitle = "Effets visuels de victoire et transitions",
                        checked = preferences.animationsEnabled,
                        onCheckedChange = { onUpdateAppSettings(it, null, null) }
                    )
                }
            }

            // 3. Audio & Musique
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Audio & Musique", style = MaterialTheme.typography.titleLarge)
                    SettingsSwitchRow(
                        title = "Musique d'ambiance",
                        subtitle = "Bande sonore adaptative douce",
                        checked = preferences.musicEnabled,
                        onCheckedChange = { onUpdateAudio(it, null, null, null, null) }
                    )
                    Text("Volume musique : ${(preferences.musicVolume * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = preferences.musicVolume,
                        onValueChange = { onUpdateAudio(null, null, it, null, null) }
                    )
                    SettingsSwitchRow(
                        title = "Effets sonores (SFX)",
                        subtitle = "Sons de réponse, chrono et victoire",
                        checked = preferences.sfxEnabled,
                        onCheckedChange = { onUpdateAudio(null, it, null, null, null) }
                    )
                    Text("Volume SFX : ${(preferences.sfxVolume * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = preferences.sfxVolume,
                        onValueChange = { onUpdateAudio(null, null, null, it, null) }
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Menu Épique", "Focus Doux", "Arène Chrono").forEach { amb ->
                            FilterChip(
                                selected = preferences.musicAmbiance == amb,
                                onClick = { onUpdateAudio(null, null, null, null, amb) },
                                label = { Text(amb) }
                            )
                        }
                    }
                }
            }

            // 4. Confidentialité, Mineurs & Interactions
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Confidentialité & Sécurité", style = MaterialTheme.typography.titleLarge)
                    SettingsSwitchRow(
                        title = "Profil public",
                        subtitle = "Visible dans la recherche d'amis",
                        checked = profile?.isPublic ?: true,
                        onCheckedChange = { onUpdatePrivacy(it, null, null, null, null) }
                    )
                    SettingsSwitchRow(
                        title = "Afficher mes statistiques",
                        subtitle = "Partager taux de victoire et division",
                        checked = profile?.showStats ?: true,
                        onCheckedChange = { onUpdatePrivacy(null, it, null, null, null) }
                    )
                    SettingsSwitchRow(
                        title = "Autoriser les invitations",
                        subtitle = "Recevoir des défis et liens de salon",
                        checked = profile?.allowInvites ?: true,
                        onCheckedChange = { onUpdatePrivacy(null, null, it, null, null) }
                    )
                    SettingsSwitchRow(
                        title = "QuizReactions en partie",
                        subtitle = "Afficher et envoyer des réactions emoji",
                        checked = profile?.reactionsEnabled ?: true,
                        onCheckedChange = { onUpdatePrivacy(null, null, null, it, null) }
                    )
                    SettingsSwitchRow(
                        title = "Mode Protection Mineurs",
                        subtitle = "Filtre strict et zéro publicité personnalisée",
                        checked = profile?.isMinorSafe ?: false,
                        onCheckedChange = { onUpdatePrivacy(null, null, null, null, it) }
                    )
                    SettingsSwitchRow(
                        title = "Notifications",
                        subtitle = "Défis, tournois et séries quotidiennes",
                        checked = preferences.notificationsEnabled,
                        onCheckedChange = { onUpdateAppSettings(null, it, null) }
                    )
                    Text(
                        "Joueurs bloqués : $blockedCount • En sourdine : $mutedCount",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 5. Publicités & Conformité Play Store
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Publicités & Monétisation (Mode Test)", style = MaterialTheme.typography.titleLarge)
                    SettingsSwitchRow(
                        title = "Emplacements AdMob post-partie",
                        subtitle = "Jamais pendant les questions ni le chronomètre",
                        checked = preferences.adsEnabled,
                        onCheckedChange = { onUpdateAppSettings(null, null, it) }
                    )
                    OutlinedButton(
                        onClick = onGrantTestRewardCoins,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tester la récompense optionnelle (+50 QuizCoins)")
                    }
                }
            }

            // 6. Tableau de bord Administrateur (Accès restreint)
            if (isAdminUser) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldAccent)
                            Text("Administration QuizFriend", style = MaterialTheme.typography.titleLarge)
                        }
                        Text(
                            "Banque de questions active : ${QuestionBank.allQuestions.size} questions vérifiées sur ${QuestionBank.allCategories.size - 1} catégories.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Règles Firestore déployées • Protection mineurs et modération actives • Aucun sponsor fictif ni identifiant publicitaire codé en dur.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmeraldSuccess
                        )
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer le compte ?") },
            text = { Text("Cette action efface votre profil et vos données associées de manière irréversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                ) {
                    Text("Confirmer la suppression")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
