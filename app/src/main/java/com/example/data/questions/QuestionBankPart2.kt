package com.example.data.questions

import com.example.data.model.DifficultyLevel
import com.example.data.model.QuizQuestion

internal object QuestionBankPart2 {
    val curatedQuestions: List<QuizQuestion> = listOf(
        // Sports, Football, Basketball, Tennis
        QuizQuestion(
            id = "spo_1",
            question = "Combien de joueurs composent une équipe de football sur le terrain au coup d'envoi ?",
            options = listOf("9", "10", "11", "12"),
            correctIndex = 2,
            explanation = "Une équipe de football aligne 11 joueurs sur le terrain, dont un gardien de but.",
            category = "Football",
            difficulty = DifficultyLevel.EASY,
            source = "Lois du jeu IFAB / FIFA"
        ),
        QuizQuestion(
            id = "spo_2",
            question = "Quelle hauteur officielle sépare le sol de l'arceau d'un panier de basketball ?",
            options = listOf("2,85 m", "3,05 m", "3,25 m", "3,50 m"),
            correctIndex = 1,
            explanation = "La hauteur réglementaire d'un panier de basketball FIBA/NBA est de 3,05 mètres (10 pieds).",
            category = "Basketball",
            difficulty = DifficultyLevel.MEDIUM,
            source = "Règlement FIBA / NBA"
        ),
        QuizQuestion(
            id = "spo_3",
            question = "Sur quelle surface se joue le tournoi du Grand Chelem de Roland-Garros ?",
            options = listOf("Gazon", "Dur", "Terre battue", "Moquette"),
            correctIndex = 2,
            explanation = "Les Internationaux de France (Roland-Garros) sont le seul tournoi du Grand Chelem disputé sur terre battue.",
            category = "Tennis",
            difficulty = DifficultyLevel.EASY,
            source = "FFT / ITF"
        ),
        QuizQuestion(
            id = "spo_4",
            question = "Quelle distance exacte mesure un marathon olympique ?",
            options = listOf("40,195 km", "42,195 km", "45,000 km", "38,500 km"),
            correctIndex = 1,
            explanation = "La distance officielle du marathon est fixée à 42,195 km depuis 1921.",
            category = "Sports",
            difficulty = DifficultyLevel.EASY,
            source = "World Athletics"
        ),
        // Cinéma, Séries, Musique, Jeux vidéo
        QuizQuestion(
            id = "cin_1",
            question = "Qui a réalisé le film d'animation 'Le Voyage de Chihiro' (2001) ?",
            options = listOf("Makoto Shinkai", "Hayao Miyazaki", "Mamoru Hosoda", "Isao Takahata"),
            correctIndex = 1,
            explanation = "Hayao Miyazaki a écrit et réalisé 'Le Voyage de Chihiro' au sein du Studio Ghibli.",
            category = "Cinéma",
            difficulty = DifficultyLevel.EASY,
            source = "Studio Ghibli / Académie des Oscars"
        ),
        QuizQuestion(
            id = "ser_1",
            question = "Dans la série 'Breaking Bad', quelle matière enseigne Walter White au lycée ?",
            options = listOf("La physique", "La biologie", "La chimie", "Les mathématiques"),
            correctIndex = 2,
            explanation = "Walter White est professeur de chimie au lycée d'Albuquerque au début de la série.",
            category = "Séries",
            difficulty = DifficultyLevel.EASY,
            source = "AMC / Breaking Bad"
        ),
        QuizQuestion(
            id = "mus_1",
            question = "Quel instrument à cordes pincées d'Afrique de l'Ouest comporte généralement 21 cordes ?",
            options = listOf("Le balafon", "La kora", "Le djembé", "Le tama"),
            correctIndex = 1,
            explanation = "La kora est une harpe-luth traditionnelle mandingue fabriquée avec une grande calebasse et 21 cordes.",
            category = "Musique",
            difficulty = DifficultyLevel.MEDIUM,
            source = "Ethnomusicologie"
        ),
        QuizQuestion(
            id = "jv_1",
            question = "Dans quel jeu vidéo incarne-t-on Link pour sauver le royaume d'Hyrule ?",
            options = listOf("Final Fantasy", "The Legend of Zelda", "Metroid", "Xenoblade"),
            correctIndex = 1,
            explanation = "Link est le protagoniste récurrent de la saga The Legend of Zelda créée par Shigeru Miyamoto.",
            category = "Jeux vidéo",
            difficulty = DifficultyLevel.EASY,
            source = "Nintendo"
        ),
        // Animaux, Nature, Gastronomie, Art, Architecture, Mythologie
        QuizQuestion(
            id = "ani_1",
            question = "Quel mammifère marin est le plus grand animal vivant sur Terre ?",
            options = listOf("Le cachalot", "L'orque", "La baleine bleue", "Le requin-baleine"),
            correctIndex = 2,
            explanation = "La baleine bleue (Balaenoptera musculus) peut dépasser 30 mètres et 170 tonnes.",
            category = "Animaux",
            difficulty = DifficultyLevel.EASY,
            source = "UICN / National Geographic"
        ),
        QuizQuestion(
            id = "nat_1",
            question = "Par quel processus les plantes vertes convertissent-elles l'énergie lumineuse en énergie chimique ?",
            options = listOf("La fermentation", "La photosynthèse", "La transpiration", "La nitrification"),
            correctIndex = 1,
            explanation = "La photosynthèse utilise l'eau, le CO2 et la lumière pour produire du glucose et du dioxygène.",
            category = "Nature",
            difficulty = DifficultyLevel.EASY,
            source = "Botanique générale"
        ),
        QuizQuestion(
            id = "gas_1",
            question = "De quel pays est originaire le fromage mozzarella di bufala ?",
            options = listOf("France", "Espagne", "Italie", "Grèce"),
            correctIndex = 2,
            explanation = "La mozzarella di bufala est originaire de Campanie et du Latium en Italie.",
            category = "Gastronomie",
            difficulty = DifficultyLevel.EASY,
            source = "AOP Européenne"
        ),
        QuizQuestion(
            id = "art_1",
            question = "Qui a peint 'La Nuit étoilée' en 1889 ?",
            options = listOf("Claude Monet", "Pablo Picasso", "Vincent van Gogh", "Paul Cézanne"),
            correctIndex = 2,
            explanation = "Vincent van Gogh a peint 'La Nuit étoilée' à Saint-Rémy-de-Provence en juin 1889.",
            category = "Art",
            difficulty = DifficultyLevel.EASY,
            source = "MoMA New York"
        ),
        QuizQuestion(
            id = "arc_1",
            question = "Dans quelle ville se trouve la Sagrada Família conçue par Antoni Gaudí ?",
            options = listOf("Madrid", "Valence", "Barcelone", "Séville"),
            correctIndex = 2,
            explanation = "La basilique de la Sagrada Família est l'œuvre emblématique d'Antoni Gaudí à Barcelone.",
            category = "Architecture",
            difficulty = DifficultyLevel.EASY,
            source = "UNESCO"
        ),
        QuizQuestion(
            id = "myt_1",
            question = "Dans la mythologie grecque, quelle déesse est associée à la sagesse et à la stratégie militaire ?",
            options = listOf("Héra", "Artémis", "Athéna", "Aphrodite"),
            correctIndex = 2,
            explanation = "Athéna est la déesse de la sagesse, de l'artisanat et de la stratégie guerrière.",
            category = "Mythologie",
            difficulty = DifficultyLevel.EASY,
            source = "Mythologie grecque"
        ),
        // Logique, Devinettes, Culture générale, Culture mondiale
        QuizQuestion(
            id = "log_1",
            question = "Si tous les Bloops sont des Razzies et tous les Razzies sont des Lazzies, alors tous les Bloops sont :",
            options = listOf("Des Lazzies", "Jamais des Lazzies", "Parfois des Razzies", "Impossible à savoir"),
            correctIndex = 0,
            explanation = "Par transitivité de l'inclusion logique (A ⊂ B et B ⊂ C ⇒ A ⊂ C), tous les Bloops sont des Lazzies.",
            category = "Logique",
            difficulty = DifficultyLevel.EASY,
            source = "Syllogisme classique"
        ),
        QuizQuestion(
            id = "dev_1",
            question = "Je parle toutes les langues mais je n'ai pas de bouche. Je réponds quand on m'appelle. Qui suis-je ?",
            options = listOf("Le vent", "L'écho", "Le miroir", "L'ombre"),
            correctIndex = 1,
            explanation = "L'écho renvoie les sons et les mots dans n'importe quelle langue dès qu'on lui parle.",
            category = "Devinettes",
            difficulty = DifficultyLevel.EASY,
            source = "Patrimoine des devinettes"
        ),
        QuizQuestion(
            id = "cg_1",
            question = "Quel océan sépare l'Afrique de l'Australie ?",
            options = listOf("L'océan Atlantique", "L'océan Pacifique", "L'océan Indien", "L'océan Arctique"),
            correctIndex = 2,
            explanation = "L'océan Indien s'étend entre la côte est de l'Afrique, l'Asie du Sud et l'Australie.",
            category = "Culture générale",
            difficulty = DifficultyLevel.EASY,
            source = "OHI"
        ),
        QuizQuestion(
            id = "cm_1",
            question = "Quel festival japonais célèbre la floraison des cerisiers au printemps ?",
            options = listOf("Obon", "Hanami", "Tanabata", "Setsubun"),
            correctIndex = 1,
            explanation = "Le Hanami ('regarder les fleurs') est la tradition japonaise d'admirer les cerisiers en fleurs (sakura).",
            category = "Culture mondiale",
            difficulty = DifficultyLevel.MEDIUM,
            source = "Patrimoine culturel du Japon"
        ),
        QuizQuestion(
            id = "his_1",
            question = "En quelle année a eu lieu le premier vol habité ayant permis à des humains de marcher sur la Lune ?",
            options = listOf("1965", "1967", "1969", "1972"),
            correctIndex = 2,
            explanation = "Apollo 11 a aluni le 20 juillet 1969 avec Neil Armstrong et Buzz Aldrin.",
            category = "Histoire",
            difficulty = DifficultyLevel.EASY,
            source = "NASA History"
        ),
        QuizQuestion(
            id = "geo_1",
            question = "Quelle est la capitale administrative de la Suisse (ville fédérale) ?",
            options = listOf("Zurich", "Genève", "Berne", "Bâle"),
            correctIndex = 2,
            explanation = "Berne est la ville fédérale abritant le siège du gouvernement et du parlement suisses.",
            category = "Géographie",
            difficulty = DifficultyLevel.MEDIUM,
            source = "Confédération suisse"
        )
    )
}
