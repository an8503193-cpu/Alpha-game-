package com.example.data.questions

import com.example.data.model.DifficultyLevel
import com.example.data.model.QuizQuestion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object QuestionBank {

    val allCategories: List<String> = listOf(
        "Toutes",
        "Sciences",
        "Mathématiques",
        "Physique",
        "Chimie",
        "Biologie",
        "Astronomie",
        "Géographie",
        "Histoire",
        "Culture générale",
        "Technologie",
        "Informatique",
        "Intelligence artificielle",
        "Programmation",
        "Sports",
        "Football",
        "Basketball",
        "Tennis",
        "Cinéma",
        "Séries",
        "Musique",
        "Jeux vidéo",
        "Animaux",
        "Nature",
        "Gastronomie",
        "Art",
        "Architecture",
        "Mythologie",
        "Logique",
        "Devinettes",
        "Culture africaine",
        "Culture sénégalaise",
        "Culture mondiale"
    )

    val allQuestions: List<QuizQuestion> by lazy {
        (QuestionBankPart1.curatedQuestions +
            QuestionBankPart2.curatedQuestions +
            QuestionBankPart3.curatedQuestions +
            QuestionBankGenerator.generateVerifiedBank())
            .distinctBy { it.id }
    }

    fun selectQuestions(
        category: String,
        difficulty: DifficultyLevel,
        count: Int
    ): List<QuizQuestion> {
        val catFiltered = if (category == "Toutes" || category.isBlank()) {
            allQuestions
        } else {
            val exact = allQuestions.filter { it.category.equals(category, ignoreCase = true) }
            if (exact.size >= count) exact else exact + allQuestions.filter { it !in exact }.shuffled()
        }
        val diffFiltered = if (difficulty == DifficultyLevel.MIXED) {
            catFiltered
        } else {
            val matching = catFiltered.filter { it.difficulty == difficulty }
            if (matching.size >= count) matching else matching + catFiltered.filter { it !in matching }
        }
        return diffFiltered.shuffled().take(count.coerceAtLeast(1))
    }

    fun getDailyQuizQuestions(count: Int = 10): List<QuizQuestion> {
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val seed = dateStr.toLongOrNull() ?: 20261115L
        return allQuestions.shuffled(Random(seed)).take(count)
    }

    fun findById(id: String): QuizQuestion? = allQuestions.firstOrNull { it.id == id }
}
