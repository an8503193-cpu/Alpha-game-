package com.example.data.ai

import android.util.Log
import com.example.data.model.DifficultyLevel
import com.example.data.model.QuizQuestion
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

data class AiCoachDiagnosis(
    val summary: String,
    val strongCategories: List<String>,
    val weakCategories: List<String>,
    val recommendedCategory: String,
    val studyTip: String
)

class AiCoachRepository {

    private val textModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val diagnosisSchema = Schema.obj(
        properties = mapOf(
            "summary" to Schema.string(description = "Résumé motivant en français des performances du joueur"),
            "strongCategories" to Schema.array(Schema.string(), description = "Catégories maîtrisées"),
            "weakCategories" to Schema.array(Schema.string(), description = "Catégories à réviser"),
            "recommendedCategory" to Schema.string(description = "Catégorie prioritaire recommandée"),
            "studyTip" to Schema.string(description = "Conseil pédagogique concret")
        )
    )

    private val diagnosisModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = diagnosisSchema
            }
        )
    }

    private val exerciseSchema = Schema.obj(
        properties = mapOf(
            "question" to Schema.string(description = "Question claire en français"),
            "options" to Schema.array(Schema.string(), description = "Exactement 4 propositions distinctes"),
            "correctIndex" to Schema.integer(description = "Indice 0 à 3 de la bonne réponse"),
            "explanation" to Schema.string(description = "Explication pédagogique détaillée")
        )
    )

    private val exerciseModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = exerciseSchema
            }
        )
    }

    suspend fun analyzePlayerPerformance(
        pseudo: String,
        level: Long,
        winRatePercent: Int,
        accuracyPercent: Int,
        weakCategories: List<String>,
        strongCategories: List<String>
    ): Result<AiCoachDiagnosis> {
        return try {
            val prompt = """
                Tu es le Coach IA bienveillant et rigoureux de QuizFriend (application éducative francophone).
                Analyse ces statistiques du joueur $pseudo (Niveau $level, Taux de victoire $winRatePercent%, Précision $accuracyPercent%).
                Points forts observés : ${strongCategories.ifEmpty { listOf("Culture générale") }.joinToString(", ")}.
                Points faibles observés : ${weakCategories.ifEmpty { listOf("Sciences", "Histoire") }.joinToString(", ")}.
                Note : tu ne modifies jamais les scores ni les classements officiels.
                Réponds en français clair et encourageant.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                diagnosisModel.generateContent(prompt)
            }
            val text = response.text ?: throw IllegalStateException("Réponse vide du Coach IA.")
            val json = JSONObject(text)
            val strongArr = json.getJSONArray("strongCategories")
            val weakArr = json.getJSONArray("weakCategories")
            Result.success(
                AiCoachDiagnosis(
                    summary = json.getString("summary"),
                    strongCategories = (0 until strongArr.length()).map { strongArr.getString(it) },
                    weakCategories = (0 until weakArr.length()).map { weakArr.getString(it) },
                    recommendedCategory = json.getString("recommendedCategory"),
                    studyTip = json.getString("studyTip")
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Coach analysis timed out", e)
            Result.failure(Exception("Le Coach IA a mis trop de temps à répondre (60s). Réessayez."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Coach analysis failed", e)
            Result.failure(e)
        }
    }

    fun streamWrongAnswerExplanation(
        question: QuizQuestion,
        chosenOption: String?
    ): Flow<String> = flow {
        val prompt = """
            Explique de manière pédagogique, concise et encourageante en français pourquoi la bonne réponse à cette question est "${question.options[question.correctIndex]}" :
            Question : ${question.question}
            Réponse choisie par le joueur : ${chosenOption ?: "Temps écoulé"}
            Explication de référence : ${question.explanation}
            Donne un moyen mnémotechnique simple à la fin.
        """.trimIndent()
        val acc = StringBuilder()
        withTimeout(60_000L) {
            textModel.generateContentStream(prompt).collect { chunk ->
                val part = chunk.text.orEmpty()
                if (part.isNotEmpty()) {
                    acc.append(part)
                    emit(acc.toString())
                }
            }
        }
    }

    suspend fun generateTrainingExercise(
        category: String,
        difficulty: DifficultyLevel
    ): Result<QuizQuestion> {
        return try {
            val prompt = """
                Génère une question de quiz éducative vérifiée et sans ambiguïté en français.
                Catégorie : $category
                Niveau : ${difficulty.label}
                Contraintes : exactement 4 propositions distinctes, une seule réponse objectivement exacte (correctIndex entre 0 et 3), et une explication pédagogique claire.
            """.trimIndent()
            val response = withTimeout(60_000L) {
                exerciseModel.generateContent(prompt)
            }
            val raw = response.text ?: throw IllegalStateException("Exercice vide.")
            val json = JSONObject(raw)
            val optsArr = json.getJSONArray("options")
            val options = (0 until optsArr.length()).map { optsArr.getString(it) }.take(4)
            require(options.size == 4) { "Le modèle n'a pas renvoyé 4 propositions." }
            val idx = json.optInt("correctIndex", 0).coerceIn(0, 3)
            Result.success(
                QuizQuestion(
                    id = "ai_${System.currentTimeMillis()}",
                    question = json.getString("question"),
                    options = options,
                    correctIndex = idx,
                    explanation = json.getString("explanation"),
                    category = category,
                    difficulty = difficulty,
                    source = "Exercice généré par IA (vérifié en mode entraînement)",
                    isAiGenerated = true
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Exercise generation timed out", e)
            Result.failure(Exception("Délai dépassé lors de la génération de l'exercice."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Exercise generation failed", e)
            Result.failure(e)
        }
    }

    private companion object {
        const val TAG = "AiCoachRepository"
    }
}
