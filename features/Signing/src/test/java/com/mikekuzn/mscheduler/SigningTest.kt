package com.mikekuzn.mscheduler

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mikekuzn.resource.R
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SigningTest {

    private lateinit var signing: Signing
    private val context = mockk<Context>()
    private val useCases = mockk<UseCasesInter>()
    private val firebaseUser = mockk<FirebaseUser>()
    private val firebaseAuth = mockk<FirebaseAuth>()
    private val authResult = mockk<Task<AuthResult>>()
    private val message1 = "Any message1"
    private val message2 = "Any message2"

    private fun getSigning() = Signing(
        context,
        useCases,
    )

    @Before
    fun init() {
        mockkStatic(FirebaseAuth::class)
        every { context.getString(R.string.incorrectEmail) } returns message1
        every { context.getString(R.string.incorrectPass) } returns message2
        every { FirebaseAuth.getInstance() } returns firebaseAuth
        every { firebaseAuth.addAuthStateListener(any()) } returns Unit
        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseAuth.removeAuthStateListener(any()) } returns Unit
        every { firebaseAuth.createUserWithEmailAndPassword(any(), any()) } returns authResult
        every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns authResult
        every { firebaseAuth.signOut() } returns Unit
        every { authResult.addOnCompleteListener(any()) } returns authResult
        every { authResult.addOnFailureListener(any()) } returns authResult
        every { firebaseUser.sendEmailVerification() } returns mockk<Task<Void>>()
        every { useCases.clrUserPath() } returns Unit
        signing = getSigning()
    }

    @Test
    fun unsubscribe() {
        getSigning().unsubscribe()
        verify { firebaseAuth.removeAuthStateListener(any()) }
    }

    @Test
    fun `signUp Incorrect1`() {
        signing.signUp("Incorrect", "pass")
        verify(exactly = 0) { firebaseAuth.createUserWithEmailAndPassword(any(), any()) }
        Assert.assertEquals(message1, signing.messageOrState)
    }

    @Test
    fun `signUp Incorrect2`() {
        signing.signUp("Incorrect@mail.", "pass")
        verify(exactly = 0) { firebaseAuth.createUserWithEmailAndPassword(any(), any()) }
        Assert.assertEquals(message1, signing.messageOrState)
    }

    @Test
    fun `signUp Incorrect3`() {
        signing.signUp("Correct@mail.ru", "pass")
        verify(exactly = 0) { firebaseAuth.createUserWithEmailAndPassword(any(), any()) }
        Assert.assertEquals(message2, signing.messageOrState)
    }

    @Test
    fun `signUp`() {
        signing.signUp("Correct@mail.ru", "CorrectPass")
        verify(exactly = 1) { firebaseAuth.createUserWithEmailAndPassword(any(), any()) }
        Assert.assertEquals(null, signing.messageOrState)
    }

    @Test
    fun `signIn`() {
        signing.signIn("Correct@mail.ru", "CorrectPass")
        verify(exactly = 1) { firebaseAuth.signInWithEmailAndPassword(any(), any()) }
        Assert.assertEquals(null, signing.messageOrState)
    }

    @Test
    fun `signOut`() {
        signing.signOut()
        verify { useCases.clrUserPath() }
        verify { firebaseAuth.signOut() }
    }

    @Test
    fun `sendMailVerification`() {
        signing.sendMailVerification()
        verify { firebaseUser.sendEmailVerification() }
    }
}
