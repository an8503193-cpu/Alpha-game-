package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizFriendRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun ensureUserProfile_and_observeUserProfile_succeeds() = runBlocking {
        val uid = signInTestUser("alice@quizfriend.test")
        val repo = QuizFriendRepository(firestore)

        val profile = withTimeout(5000L) {
            repo.ensureUserProfile(uid, "AliceTest", "alice@quizfriend.test")
        }
        assertEquals(uid, profile.userId)
        assertTrue(profile.quizCoins >= 300L)

        val observed = withTimeout(3000L) {
            repo.observeUserProfile(uid).first { it != null }
        }
        assertNotNull(observed)
        assertEquals("AliceTest", observed?.pseudo)
    }

    @Test
    fun createRoom_and_joinRoom_succeeds() = runBlocking {
        val aliceUid = signInTestUser("alice2@quizfriend.test")
        val repo = QuizFriendRepository(firestore)
        val aliceProfile = withTimeout(5000L) {
            repo.ensureUserProfile(aliceUid, "AliceHost", "alice2@quizfriend.test")
        }

        val roomId = withTimeout(5000L) {
            repo.createRoom(
                hostProfile = aliceProfile,
                mode = "Duel",
                category = "Sciences",
                difficulty = "Moyen",
                questionCount = 10L,
                timePerQuestionSec = 15L,
                maxPlayers = 2L,
                questionIds = listOf("q1", "q2")
            )
        }
        val room = withTimeout(3000L) {
            repo.observeRoom(roomId).first { it != null }
        }!!
        assertEquals(6, room.roomCode.length)

        val bobUid = signInTestUser("bob2@quizfriend.test")
        val bobProfile = withTimeout(5000L) {
            repo.ensureUserProfile(bobUid, "BobJoin", "bob2@quizfriend.test")
        }
        val joinedRoomId = withTimeout(5000L) {
            repo.joinRoomByCode(room.roomCode, bobProfile)
        }
        assertEquals(roomId, joinedRoomId)
    }

    @Test
    fun observeHistory_unauthenticated_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repo = QuizFriendRepository(firestore)
        val error = try {
            withTimeout(3000L) {
                repo.observeMatchHistory("some_uid").first()
            }
            null
        } catch (e: Exception) {
            e
        }
        assertNotNull(error)
        val fsError = error!!.unwrapFirestoreException()
        assertNotNull(fsError)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsError!!.code)
    }

    @Test
    fun crossUserPrivateInfo_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser("alice3@quizfriend.test")
        val repo = QuizFriendRepository(firestore)
        withTimeout(5000L) {
            repo.ensureUserProfile(aliceUid, "AlicePriv", "alice3@quizfriend.test")
        }

        signInTestUser("bob3@quizfriend.test")
        val exception = try {
            withTimeout(5000L) {
                firestore.collection("users").document(aliceUid)
                    .collection("private").document("info").get().await()
            }
            null
        } catch (e: FirebaseFirestoreException) {
            e
        }
        assertNotNull(exception)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, exception!!.code)
    }
}
