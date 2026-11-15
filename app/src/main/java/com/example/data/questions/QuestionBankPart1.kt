package com.example.data.questions

import com.example.data.model.DifficultyLevel
import com.example.data.model.QuizQuestion

internal object QuestionBankPart1 {
    val curatedQuestions: List<QuizQuestion> = listOf(
        // Sciences & Physique & Chimie & Biologie & Astronomie
        QuizQuestion(
            id = "sci_1",
            question = "Quelle particule subatomique porte une charge électrique négative ?",
            options = listOf("Le proton", "L'électron", "Le neutron", "Le photon"),
            correctIndex = 1,
            explanation = "L'électron possède une charge électrique négative (-1e), tandis que le proton est positif (+1e) et le neutron est neutre.",
            category = "Sciences",
            difficulty = DifficultyLevel.EASY,
            source = "UICPA / Manuel de physique générale"
        ),
        QuizQuestion(
            id = "sci_2",
            question = "Quelle est la formule chimique du dioxyde de carbone ?",
            options = listOf("CO", "CO2", "C2O", "O2C"),
            correctIndex = 1,
            explanation = "Le dioxyde de carbone est composé d'un atome de carbone lié à deux atomes d'oxygène (CO2).",
            category = "Chimie",
            difficulty = DifficultyLevel.EASY,
            source = "UICPA"
        ),
        QuizQuestion(
            id = "sci_3",
            question = "Quelle planète du Système solaire possède la plus grande masse ?",
            options = listOf("Saturne", "Neptune", "Jupiter", "Uranus"),
            correctIndex = 2,
            explanation = "Jupiter est la plus massive des planètes du Système solaire, représentant plus de deux fois la masse de toutes les autres réunies.",
            category = "Astronomie",
            difficulty = DifficultyLevel.EASY,
            source = "NASA / IAU"
        ),
        QuizQuestion(
            id = "sci_4",
            question = "Quel organite cellulaire produit la majorité de l'ATP par respiration cellulaire ?",
            options = listOf("Le ribosome", "L'appareil de Golgi", "La mitochondrie", "Le lysosome"),
            correctIndex = 2,
            explanation = "La mitochondrie est la centrale énergétique des cellules eucaryotes grâce à la phosphorylation oxydative.",
            category = "Biologie",
            difficulty = DifficultyLevel.MEDIUM,
            source = "Biologie cellulaire - Alberts"
        ),
        QuizQuestion(
            id = "sci_5",
            question = "Quelle loi relie la tension U, la résistance R et l'intensité I d'un conducteur ohmique ?",
            options = listOf("La loi de Coulomb", "La loi d'Ohm", "La loi de Faraday", "La loi de Kepler"),
            correctIndex = 1,
            explanation = "La loi d'Ohm s'énonce U = R × I, où U est en volts, R en ohms et I en ampères.",
            category = "Physique",
            difficulty = DifficultyLevel.EASY,
            source = "SI / Électrocinétique"
        ),
        QuizQuestion(
            id = "sci_6",
            question = "Quel élément chimique a pour symbole Au ?",
            options = listOf("L'argent", "L'aluminium", "L'or", "L'argon"),
            correctIndex = 2,
            explanation = "Au provient du latin 'aurum', qui signifie or (numéro atomique 79).",
            category = "Chimie",
            difficulty = DifficultyLevel.EASY,
            source = "Tableau périodique UICPA"
        ),
        QuizQuestion(
            id = "sci_7",
            question = "À quelle vitesse approximative la lumière se propage-t-elle dans le vide ?",
            options = listOf("30 000 km/s", "150 000 km/s", "300 000 km/s", "1 000 000 km/s"),
            correctIndex = 2,
            explanation = "La constante c vaut exactement 299 792 458 m/s, soit environ 300 000 km/s.",
            category = "Physique",
            difficulty = DifficultyLevel.EASY,
            source = "BIPM"
        ),
        QuizQuestion(
            id = "sci_8",
            question = "Combien de paires de chromosomes possède une cellule somatique humaine saine ?",
            options = listOf("21 paires", "23 paires", "24 paires", "46 paires"),
            correctIndex = 1,
            explanation = "L'être humain possède 46 chromosomes organisés en 23 paires (22 autosomes + 1 paire sexuelle).",
            category = "Biologie",
            difficulty = DifficultyLevel.EASY,
            source = "Génétique humaine"
        ),
        QuizQuestion(
            id = "sci_9",
            question = "Quelle est l'étoile la plus proche de la Terre après le Soleil ?",
            options = listOf("Sirius", "Proxima du Centaure", "Bételgeuse", "Vega"),
            correctIndex = 1,
            explanation = "Proxima du Centaure est située à environ 4,24 années-lumière du Système solaire.",
            category = "Astronomie",
            difficulty = DifficultyLevel.MEDIUM,
            source = "ESA / ESO"
        ),
        QuizQuestion(
            id = "sci_10",
            question = "Quel gaz est le plus abondant dans l'atmosphère terrestre ?",
            options = listOf("Oxygène", "Dioxyde de carbone", "Diazote", "Argon"),
            correctIndex = 2,
            explanation = "L'atmosphère sèche contient environ 78 % de diazote (N2) et 21 % de dioxygène (O2).",
            category = "Sciences",
            difficulty = DifficultyLevel.EASY,
            source = "Météo-France / WMO"
        ),
        // Mathématiques
        QuizQuestion(
            id = "math_1",
            question = "Quelle est la valeur de la dérivée de f(x) = x³ ?",
            options = listOf("x²", "2x²", "3x²", "3x"),
            correctIndex = 2,
            explanation = "La dérivée de x^n est n·x^(n-1), donc pour n=3 on obtient 3x².",
            category = "Mathématiques",
            difficulty = DifficultyLevel.MEDIUM,
            source = "Analyse mathématique"
        ),
        QuizQuestion(
            id = "math_2",
            question = "Combien de degrés mesure la somme des angles intérieurs d'un triangle euclidien ?",
            options = listOf("90°", "180°", "270°", "360°"),
            correctIndex = 1,
            explanation = "Dans un plan euclidien, la somme des trois angles d'un triangle vaut toujours 180°.",
            category = "Mathématiques",
            difficulty = DifficultyLevel.EASY,
            source = "Géométrie euclidienne"
        ),
        QuizQuestion(
            id = "math_3",
            question = "Quel est le plus petit nombre premier pair ?",
            options = listOf("0", "1", "2", "4"),
            correctIndex = 2,
            explanation = "2 est divisible uniquement par 1 et par lui-même ; c'est le seul nombre premier pair.",
            category = "Mathématiques",
            difficulty = DifficultyLevel.EASY,
            source = "Arithmétique"
        ),
        // Technologie, Informatique, IA, Programmation
        QuizQuestion(
            id = "tech_1",
            question = "Que signifie l'acronyme HTTP ?",
            options = listOf(
                "HyperText Transfer Protocol",
                "High Transmission Text Process",
                "Hyperlink Terminal Transport Protocol",
                "Host Transfer Text Port"
            ),
            correctIndex = 0,
            explanation = "HTTP signifie HyperText Transfer Protocol, le protocole de communication du Web.",
            category = "Informatique",
            difficulty = DifficultyLevel.EASY,
            source = "RFC 9110 / IETF"
        ),
        QuizQuestion(
            id = "tech_2",
            question = "Quelle structure de données fonctionne selon le principe LIFO (Last In, First Out) ?",
            options = listOf("La file (Queue)", "La pile (Stack)", "L'arbre binaire", "Le graphe"),
            correctIndex = 1,
            explanation = "Dans une pile (Stack), le dernier élément ajouté est le premier retiré.",
            category = "Programmation",
            difficulty = DifficultyLevel.MEDIUM,
            source = "Algorithmique"
        ),
        QuizQuestion(
            id = "tech_3",
            question = "Quel type d'architecture neuronale est à la base des grands modèles de langage modernes (LLM) ?",
            options = listOf("Perceptron simple", "Transformer", "Arbre de décision", "K-Means"),
            correctIndex = 1,
            explanation = "L'architecture Transformer (Vaswani et al., 2017) repose sur le mécanisme d'auto-attention.",
            category = "Intelligence artificielle",
            difficulty = DifficultyLevel.MEDIUM,
            source = "Attention Is All You Need (2017)"
        ),
        QuizQuestion(
            id = "tech_4",
            question = "En Kotlin, quel mot-clé déclare une variable immuable en référence ?",
            options = listOf("var", "let", "val", "const"),
            correctIndex = 2,
            explanation = "En Kotlin, 'val' déclare une référence en lecture seule (non réassignable).",
            category = "Programmation",
            difficulty = DifficultyLevel.EASY,
            source = "Documentation officielle Kotlin"
        ),
        QuizQuestion(
            id = "tech_5",
            question = "Qui est considérée comme la première programmeuse de l'histoire pour ses notes sur la machine analytique ?",
            options = listOf("Grace Hopper", "Ada Lovelace", "Margaret Hamilton", "Katherine Johnson"),
            correctIndex = 1,
            explanation = "Ada Lovelace rédigea en 1843 le premier algorithme destiné à la machine analytique de Charles Babbage.",
            category = "Technologie",
            difficulty = DifficultyLevel.EASY,
            source = "Histoire de l'informatique"
        ),
        QuizQuestion(
            id = "tech_6",
            question = "Quel protocole sécurise les échanges HTTP par chiffrement TLS ?",
            options = listOf("FTP", "HTTPS", "SMTP", "ICMP"),
            correctIndex = 1,
            explanation = "HTTPS combine HTTP avec TLS (Transport Layer Security) pour garantir confidentialité et intégrité.",
            category = "Informatique",
            difficulty = DifficultyLevel.EASY,
            source = "IETF"
        ),
        // Culture sénégalaise & Culture africaine
        QuizQuestion(
            id = "sen_1",
            question = "Quel célèbre monument de Dakar culmine à 49 mètres sur une des collines des Mamelles ?",
            options = listOf(
                "La Porte du Troisième Millénaire",
                "Le Monument de la Renaissance africaine",
                "Le Phare des Mamelles",
                "L'Obélisque de Colobane"
            ),
            correctIndex = 1,
            explanation = "Inauguré en 2010, le Monument de la Renaissance africaine est une statue monumentale en bronze et cuivre située à Ouakam, Dakar.",
            category = "Culture sénégalaise",
            difficulty = DifficultyLevel.EASY,
            source = "Patrimoine culturel du Sénégal"
        ),
        QuizQuestion(
            id = "sen_2",
            question = "Quel plat emblématique sénégalais est inscrit au patrimoine culturel immatériel de l'UNESCO depuis 2021 ?",
            options = listOf("Le Yassa", "Le Mafé", "Le Ceebu Jën", "Le Lakh"),
            correctIndex = 2,
            explanation = "Le Ceebu Jën (riz au poisson), originaire de Saint-Louis, a été inscrit par l'UNESCO en décembre 2021.",
            category = "Culture sénégalaise",
            difficulty = DifficultyLevel.EASY,
            source = "UNESCO (2021)"
        ),
        QuizQuestion(
            id = "sen_3",
            question = "En quelle année l'équipe nationale du Sénégal de football a-t-elle remporté sa première Coupe d'Afrique des Nations ?",
            options = listOf("2002", "2019", "2022", "2024"),
            correctIndex = 2,
            explanation = "Les Lions de la Téranga ont remporté la CAN 2021 (jouée en février 2022) face à l'Égypte aux tirs au but.",
            category = "Culture sénégalaise",
            difficulty = DifficultyLevel.EASY,
            source = "CAF"
        ),
        QuizQuestion(
            id = "sen_4",
            question = "Quelle île au large de Dakar est classée au patrimoine mondial de l'UNESCO pour sa valeur mémorielle ?",
            options = listOf("L'île de Ngor", "L'île de Gorée", "L'île de Carabane", "L'île aux Serpents"),
            correctIndex = 1,
            explanation = "L'île de Gorée est inscrite à l'UNESCO depuis 1978 comme symbole de la mémoire de la traite négrière.",
            category = "Culture sénégalaise",
            difficulty = DifficultyLevel.EASY,
            source = "UNESCO"
        ),
        QuizQuestion(
            id = "sen_5",
            question = "Quel poète et homme d'État sénégalais fut le premier président de la République du Sénégal ?",
            options = listOf("Cheikh Anta Diop", "Léopold Sédar Senghor", "Abdou Diouf", "Ousmane Sembène"),
            correctIndex = 1,
            explanation = "Léopold Sédar Senghor (1906-2001), cofondateur du mouvement de la Négritude, fut président du Sénégal de 1960 à 1980.",
            category = "Culture sénégalaise",
            difficulty = DifficultyLevel.EASY,
            source = "Archives nationales du Sénégal"
        ),
        QuizQuestion(
            id = "afr_1",
            question = "Quel fleuve est le plus long d'Afrique ?",
            options = listOf("Le Congo", "Le Niger", "Le Nil", "Le Zambèze"),
            correctIndex = 2,
            explanation = "Le Nil mesure environ 6 650 km de long et traverse onze pays d'Afrique du Nord-Est.",
            category = "Culture africaine",
            difficulty = DifficultyLevel.EASY,
            source = "Géographie physique de l'Afrique"
        ),
        QuizQuestion(
            id = "afr_2",
            question = "Quelle ancienne cité malienne est célèbre pour ses manuscrits médiévaux et son université historique ?",
            options = listOf("Gao", "Tombouctou", "Djenné", "Ségou"),
            correctIndex = 1,
            explanation = "Tombouctou fut un centre majeur du savoir islamique et des manuscrits entre le XIVe et le XVIe siècle.",
            category = "Culture africaine",
            difficulty = DifficultyLevel.MEDIUM,
            source = "UNESCO"
        ),
        QuizQuestion(
            id = "afr_3",
            question = "Quel massif montagneux abrite le point culminant de l'Afrique ?",
            options = listOf("Le Mont Kenya", "Le Kilimandjaro", "L'Atlas", "Le Drakensberg"),
            correctIndex = 1,
            explanation = "Le pic Uhuru sur le Kilimandjaro (Tanzanie) culmine à 5 895 mètres d'altitude.",
            category = "Culture africaine",
            difficulty = DifficultyLevel.EASY,
            source = "National Geographic"
        ),
        QuizQuestion(
            id = "afr_4",
            question = "Quelle écrivaine nigériane a remporté le prix Nobel de littérature ?",
            options = listOf("Chimamanda Ngozi Adichie", "Wole Soyinka est un écrivain", "Aucune femme nigériane encore, mais Wole Soyinka en 1986", "Nadine Gordimer"),
            correctIndex = 2,
            explanation = "Wole Soyinka (Nigeria) a reçu le prix Nobel de littérature en 1986 ; Nadine Gordimer (Afrique du Sud) en 1991.",
            category = "Culture africaine",
            difficulty = DifficultyLevel.EXPERT,
            source = "NobelPrize.org"
        )
    )
}
