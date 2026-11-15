package com.example.data.model

object QuizCatalogs {

    val shopItems: List<ShopItem> = listOf(
        // Avatars (Classiques, Gaming, Animaux, Futuristes, Fantaisie, Rares, Légendaires)
        ShopItem("avatar_classic_spark", "Éclair Classique", ShopItemType.AVATAR, "Classique", "⚡", 0L, "Avatar de départ lumineux"),
        ShopItem("avatar_gaming_pad", "Pro Gamer", ShopItemType.AVATAR, "Gaming", "🎮", 150L, "Contrôleur néon de compétition"),
        ShopItem("avatar_animal_lion", "Lion Téranga", ShopItemType.AVATAR, "Animaux", "🦁", 250L, "Courage et fierté de la savane"),
        ShopItem("avatar_animal_owl", "Chouette Savante", ShopItemType.AVATAR, "Animaux", "🦉", 220L, "Sagesse nocturne des quiz"),
        ShopItem("avatar_future_cyber", "Cyber Pilote", ShopItemType.AVATAR, "Futuriste", "🛸", 400L, "Casque holographique orbital"),
        ShopItem("avatar_fantasy_dragon", "Dragon d'Or", ShopItemType.AVATAR, "Fantaisie", "🐉", 550L, "Souffle mythique des champions"),
        ShopItem("avatar_rare_phoenix", "Phénix Astral", ShopItemType.AVATAR, "Rare", "🔥", 750L, "Renaissance flamboyante"),
        ShopItem("avatar_legend_crown", "Souverain Légende", ShopItemType.AVATAR, "Légendaire", "👑", 1200L, "Symbole ultime des maîtres du savoir"),

        // Frames
        ShopItem("frame_none", "Sans cadre", ShopItemType.FRAME, "Classique", "⭕", 0L, "Bordure épurée"),
        ShopItem("frame_neon", "Halo Néon", ShopItemType.FRAME, "Gaming", "💜", 200L, "Anneau violet électrique"),
        ShopItem("frame_gold", "Couronne Dorée", ShopItemType.FRAME, "Rare", "✨", 500L, "Cadre brillant des vainqueurs"),
        ShopItem("frame_cosmic", "Orbite Galaxie", ShopItemType.FRAME, "Légendaire", "🌌", 900L, "Aura stellaire animée"),

        // Themes
        ShopItem("dark_gaming", "Dark Gaming", ShopItemType.THEME, "Classique", "🌑", 0L, "Thème sombre haute lisibilité"),
        ShopItem("neon_purple", "Neon Purple", ShopItemType.THEME, "Gaming", "🟣", 300L, "Contraste violet & magenta"),
        ShopItem("cyber_blue", "Cyber Blue", ShopItemType.THEME, "Futuriste", "🔵", 350L, "Interface cyan futuriste"),
        ShopItem("galaxy", "Galaxy", ShopItemType.THEME, "Rare", "🌠", 500L, "Nébuleuse indigo et or"),
        ShopItem("golden_premium", "Golden Premium", ShopItemType.THEME, "Légendaire", "🏆", 800L, "Luxe noir et or"),
        ShopItem("minimal_white", "Minimal White", ShopItemType.THEME, "Classique", "🤍", 200L, "Clarté pure et lumineuse"),

        // Victory effects
        ShopItem("vfx_confetti", "Pluie de Confettis", ShopItemType.VICTORY_EFFECT, "Classique", "🎊", 0L, "Célébration festive standard"),
        ShopItem("vfx_fireworks", "Feux d'Artifice", ShopItemType.VICTORY_EFFECT, "Rare", "🎆", 450L, "Gerbes lumineuses sur le podium"),
        ShopItem("vfx_lightning", "Éclairs Royaux", ShopItemType.VICTORY_EFFECT, "Légendaire", "⚡", 700L, "Décharge épique de victoire"),

        // Titles
        ShopItem("title_rookie", "Apprenti Curieux", ShopItemType.TITLE, "Classique", "📘", 0L, "Titre de départ"),
        ShopItem("title_strategist", "Stratège Rapide", ShopItemType.TITLE, "Gaming", "🎯", 250L, "Pour les maîtres du chrono"),
        ShopItem("title_sage", "Érudit d'Afrique", ShopItemType.TITLE, "Rare", "🌍", 450L, "Expert des cultures du monde"),
        ShopItem("title_legend", "Légende QuizFriend", ShopItemType.TITLE, "Légendaire", "🌟", 1000L, "Titre prestigieux de fin de saison")
    )

    val virtualGifts: List<VirtualGift> = listOf(
        VirtualGift("gift_trophy", "Petit Trophée", "🏆", 30L),
        VirtualGift("gift_star", "Étoile Brillante", "⭐", 20L),
        VirtualGift("gift_fireworks", "Feu d'Artifice", "🎆", 50L),
        VirtualGift("gift_heart", "Cœur Fair-Play", "❤️", 15L),
        VirtualGift("gift_party", "Célébration", "🎉", 25L)
    )

    val seasonPassTiers: List<SeasonPassTier> = listOf(
        SeasonPassTier(1, 100L, "50 QuizCoins", "🪙", true),
        SeasonPassTier(2, 250L, "Cadre Halo Néon", "💜", true),
        SeasonPassTier(3, 500L, "Titre Stratège Rapide", "🎯", true),
        SeasonPassTier(4, 800L, "Avatar Chouette Savante", "🦉", true),
        SeasonPassTier(5, 1200L, "150 QuizCoins + Badge Saison", "🎖️", true),
        SeasonPassTier(6, 1700L, "Thème Cyber Blue", "🔵", true),
        SeasonPassTier(7, 2300L, "Effet Feux d'Artifice", "🎆", true),
        SeasonPassTier(8, 3000L, "Avatar Souverain Légende", "👑", true)
    )

    val tournaments: List<TournamentEvent> = listOf(
        TournamentEvent(
            id = "tour_sci",
            title = "Semaine des Sciences",
            category = "Sciences",
            stage = "Qualifications • 15 questions",
            rewardCoins = 450L,
            trophyName = "Trophée Einstein",
            participantsCount = 128,
            isSponsored = false
        ),
        TournamentEvent(
            id = "tour_foot",
            title = "Tournoi de Football",
            category = "Football",
            stage = "Demi-finales • 15 questions",
            rewardCoins = 500L,
            trophyName = "Coupe d'Or QuizFriend",
            participantsCount = 256,
            isSponsored = false
        ),
        TournamentEvent(
            id = "tour_math",
            title = "Défi Mathématique",
            category = "Mathématiques",
            stage = "Finales • 20 questions",
            rewardCoins = 600L,
            trophyName = "Compas de Diamant",
            participantsCount = 94,
            isSponsored = false
        ),
        TournamentEvent(
            id = "tour_afr",
            title = "Quiz Afrique & Téranga",
            category = "Culture africaine",
            stage = "Ouvert à tous • 15 questions",
            rewardCoins = 550L,
            trophyName = "Baobab d'Or",
            participantsCount = 312,
            isSponsored = false
        ),
        TournamentEvent(
            id = "tour_world",
            title = "Championnat Mondial",
            category = "Toutes",
            stage = "Qualifications Saison 1",
            rewardCoins = 1000L,
            trophyName = "Couronne Mondiale",
            participantsCount = 512,
            isSponsored = false
        )
    )

    fun computeBadges(profile: UserProfile): List<BadgeDefinition> = listOf(
        BadgeDefinition("first_win", "Première Victoire", "Remportez votre 1ère partie", "🥇", profile.wins >= 1),
        BadgeDefinition("ten_wins", "10 Victoires", "Accumulez 10 victoires classées", "🏆", profile.wins >= 10),
        BadgeDefinition("fifty_wins", "50 Victoires", "Atteignez 50 victoires", "👑", profile.wins >= 50),
        BadgeDefinition("hundred_games", "100 Parties", "Jouez 100 parties sur QuizFriend", "🎮", profile.gamesPlayed >= 100),
        BadgeDefinition("streak_5", "Série 5 Jours", "Connectez-vous 5 jours de suite", "🔥", profile.streakDays >= 5),
        BadgeDefinition("accuracy_80", "Précision 80%", "Maintenez 80% de réussite (20+ rép.)", "🎯", profile.totalAnswers >= 20 && (profile.correctAnswers * 100 / profile.totalAnswers.coerceAtLeast(1)) >= 80),
        BadgeDefinition("high_score", "Score Élite", "Dépassez 1 200 pts en une partie", "⚡", profile.bestScore >= 1200),
        BadgeDefinition("scholar", "Maître Curieux", "Atteignez le niveau 5", "🧠", profile.level >= 5)
    )

    fun avatarEmojiFor(avatarId: String): String =
        shopItems.firstOrNull { it.id == avatarId }?.emoji ?: "⚡"

    fun titleLabelFor(titleId: String): String =
        shopItems.firstOrNull { it.id == titleId }?.name ?: "Apprenti Curieux"
}
