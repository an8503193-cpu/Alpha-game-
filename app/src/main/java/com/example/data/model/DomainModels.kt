package com.example.data.model

enum class GameMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val maxPlayers: Int,
    val isRanked: Boolean
) {
    DUEL("Duel", "Duel 1v1", "Affrontez un joueur sur la même série et le même chrono", 2, true),
    TRIO("Trio", "Trio 1v1v1", "Trois joueurs s'affrontent pour la première place", 3, true),
    TEAM("Équipe", "Équipe 2v2", "Scores individuels et collectifs avec vos amis", 4, true),
    RANDOM("Aléatoire", "Match Aléatoire", "Trouvez un adversaire de niveau similaire ou un robot IA", 2, true),
    TRAINING("Entraînement", "Entraînement", "Jouez seul sans pression ni impact sur le classement", 1, false),
    DAILY_QUIZ("Quiz du jour", "Quiz du Jour", "Les mêmes questions pour tous avec classement quotidien", 1, true),
    QUICK_ANSWER("Réponse rapide", "Réponse Rapide", "Questions courtes, chrono serré et bonus de vitesse", 1, true),
    SPECTATOR("Spectateur", "Mode Spectateur", "Regardez une partie en direct sans affecter les scores", 6, false)
}

enum class DifficultyLevel(val label: String) {
    EASY("Facile"),
    MEDIUM("Moyen"),
    HARD("Difficile"),
    EXPERT("Expert"),
    MIXED("Mélange aléatoire")
}

enum class BotDifficulty(
    val id: String,
    val label: String,
    val pseudo: String,
    val avatarEmoji: String,
    val personality: String,
    val accuracyRate: Float,
    val avgResponseMs: Long,
    val level: Int
) {
    BEGINNER("bot_beg", "Débutant", "Nova-Bot [IA]", "🤖", "Curieux et détendu", 0.45f, 6500L, 3),
    AMATEUR("bot_ama", "Amateur", "Atlas-Bot [IA]", "🦾", "Régulier et fair-play", 0.60f, 5200L, 10),
    ADVANCED("bot_adv", "Avancé", "Vega-Bot [IA]", "⚡", "Stratège rapide", 0.74f, 4000L, 22),
    EXPERT("bot_exp", "Expert", "Orion-Bot [IA]", "🧠", "Redoutable analyste", 0.86f, 2900L, 38),
    GENIUS("bot_gen", "Génie", "Chronos-Bot [IA]", "👑", "Précision quasi-chirurgicale", 0.94f, 2100L, 60)
}

enum class Division(val label: String, val minPoints: Long, val badgeEmoji: String) {
    BRONZE("Bronze", 0L, "🥉"),
    SILVER("Argent", 500L, "🥈"),
    GOLD("Or", 1200L, "🥇"),
    PLATINUM("Platine", 2200L, "💠"),
    DIAMOND("Diamant", 3500L, "💎"),
    ELITE("Élite", 5200L, "⚡"),
    CHAMPION("Champion", 7500L, "🏆"),
    LEGEND("Légende", 10500L, "👑");

    companion object {
        fun fromPoints(points: Long): Division =
            entries.lastOrNull { points >= it.minPoints } ?: BRONZE
    }
}

enum class QuizThemeStyle(
    val id: String,
    val label: String,
    val priceCoins: Long,
    val isDark: Boolean
) {
    DARK_GAMING("dark_gaming", "Dark Gaming", 0L, true),
    NEON_PURPLE("neon_purple", "Neon Purple", 300L, true),
    CYBER_BLUE("cyber_blue", "Cyber Blue", 350L, true),
    GALAXY("galaxy", "Galaxy", 500L, true),
    GOLDEN_PREMIUM("golden_premium", "Golden Premium", 800L, true),
    MINIMAL_WHITE("minimal_white", "Minimal White", 200L, false);

    companion object {
        fun fromId(id: String): QuizThemeStyle =
            entries.firstOrNull { it.id == id } ?: DARK_GAMING
    }
}

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val category: String,
    val difficulty: DifficultyLevel,
    val source: String,
    val isAiGenerated: Boolean = false
)

data class ShopItem(
    val id: String,
    val name: String,
    val type: ShopItemType,
    val rarity: String,
    val emoji: String,
    val priceCoins: Long,
    val description: String
)

enum class ShopItemType(val label: String) {
    AVATAR("Avatars"),
    FRAME("Cadres"),
    THEME("Thèmes"),
    VICTORY_EFFECT("Effets"),
    TITLE("Titres"),
    DECORATION("Décorations")
}

data class BadgeDefinition(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val unlocked: Boolean
)

data class SeasonPassTier(
    val tier: Int,
    val xpRequired: Long,
    val rewardLabel: String,
    val rewardEmoji: String,
    val isFree: Boolean = true
)

data class TournamentEvent(
    val id: String,
    val title: String,
    val category: String,
    val stage: String,
    val rewardCoins: Long,
    val trophyName: String,
    val participantsCount: Int,
    val isSponsored: Boolean = false,
    val sponsorName: String? = null
)

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val timestampLabel: String,
    val unread: Boolean = true
)

data class VirtualGift(
    val id: String,
    val name: String,
    val emoji: String,
    val costCoins: Long
)

object QuizReactionsCatalog {
    val emojis = listOf(
        "😂", "😎", "🔥", "❤️", "👏",
        "😱", "🤔", "😭", "💀", "🏆",
        "😅", "🤯", "👀", "💪", "🎉"
    )
}
