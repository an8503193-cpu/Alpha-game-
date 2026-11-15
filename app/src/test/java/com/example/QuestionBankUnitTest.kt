package com.example

import com.example.data.model.DifficultyLevel
import com.example.data.model.Division
import com.example.data.questions.QuestionBank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionBankUnitTest {

    @Test
    fun questionBankHasAtLeast1000VerifiedQuestionsAcrossAll32Categories() {
        val questions = QuestionBank.allQuestions
        assertTrue("Expected >= 1000 questions, got ${questions.size}", questions.size >= 1000)

        val categories = QuestionBank.allCategories.drop(1) // exclude "Toutes"
        assertEquals(32, categories.size)

        val byCategory = questions.groupBy { it.category }
        for (cat in categories) {
            val count = byCategory[cat]?.size ?: 0
            assertTrue("Category '$cat' should have questions, got $count", count >= 10)
        }

        for (q in questions) {
            assertTrue("Question ${q.id} text blank", q.question.isNotBlank())
            assertEquals("Question ${q.id} must have 4 options", 4, q.options.size)
            assertTrue("Question ${q.id} valid index", q.correctIndex in 0..3)
            assertTrue("Question ${q.id} explanation blank", q.explanation.isNotBlank())
            assertTrue("Question ${q.id} source blank", q.source.isNotBlank())
        }
    }

    @Test
    fun selectQuestionsAndDailyQuizReturnRequestedCounts() {
        val selected = QuestionBank.selectQuestions("Sciences", DifficultyLevel.MIXED, 15)
        assertEquals(15, selected.size)

        val daily = QuestionBank.getDailyQuizQuestions(10)
        assertEquals(10, daily.size)
    }

    @Test
    fun divisionsMapProperly() {
        assertEquals(Division.BRONZE, Division.fromPoints(0L))
        assertEquals(Division.SILVER, Division.fromPoints(500L))
        assertEquals(Division.GOLD, Division.fromPoints(1200L))
        assertEquals(Division.PLATINUM, Division.fromPoints(2200L))
        assertEquals(Division.DIAMOND, Division.fromPoints(3500L))
        assertEquals(Division.ELITE, Division.fromPoints(5200L))
        assertEquals(Division.CHAMPION, Division.fromPoints(7500L))
        assertEquals(Division.LEGEND, Division.fromPoints(10500L))
    }
}
