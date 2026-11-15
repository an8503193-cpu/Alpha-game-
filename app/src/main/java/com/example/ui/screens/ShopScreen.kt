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
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizCatalogs
import com.example.data.model.QuizThemeStyle
import com.example.data.model.ShopItem
import com.example.data.model.ShopItemType
import com.example.data.model.UserProfile
import com.example.ui.theme.GoldAccent

@Composable
fun ShopScreen(
    profile: UserProfile?,
    previewThemeId: String?,
    onPreviewTheme: (String?) -> Unit,
    onBuyOrEquip: (ShopItem) -> Unit,
    onWatchOptionalRewardedAd: () -> Unit
) {
    var selectedType by remember { mutableStateOf<ShopItemType?>(null) }
    val items = QuizCatalogs.shopItems.filter { selectedType == null || it.type == selectedType }

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Boutique QuizFriend", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "Les QuizCoins sont 100% virtuels (non convertibles en argent réel).",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(14.dp)) {
                            Text(
                                text = "🪙 ${profile?.quizCoins ?: 0}",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldAccent,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onWatchOptionalRewardedAd,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Vidéo récompensée facultative (Mode Test AdMob : +50 🪙)")
                    }
                }
            }
        }

        if (previewThemeId != null) {
            item {
                val theme = QuizThemeStyle.fromId(previewThemeId)
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Aperçu actif : ${theme.label}", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { onPreviewTheme(null) }) {
                            Text("Quitter aperçu")
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
                FilterChip(
                    selected = selectedType == null,
                    onClick = { selectedType = null },
                    label = { Text("Tout") }
                )
                ShopItemType.entries.forEach { t ->
                    FilterChip(
                        selected = selectedType == t,
                        onClick = { selectedType = t },
                        label = { Text(t.label) }
                    )
                }
            }
        }

        items(items, key = { it.id }) { item ->
            val isEquipped = item.id == profile?.avatarId ||
                item.id == profile?.frameId ||
                item.id == profile?.themeId ||
                item.id == profile?.titleId
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(item.emoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(6.dp)) {
                                    Text(
                                        text = item.rarity,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(item.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (item.type == ShopItemType.THEME) {
                            OutlinedButton(onClick = { onPreviewTheme(item.id) }) {
                                Icon(Icons.Default.Preview, contentDescription = "Aperçu")
                            }
                        }
                        Button(
                            onClick = { onBuyOrEquip(item) },
                            enabled = !isEquipped,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                when {
                                    isEquipped -> "Équipé"
                                    item.priceCoins == 0L -> "Équiper"
                                    else -> "${item.priceCoins} 🪙"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
