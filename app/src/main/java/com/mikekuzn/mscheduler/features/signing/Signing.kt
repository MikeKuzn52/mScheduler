package com.mikekuzn.mscheduler.features.signing

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.mikekuzn.mscheduler.R
import com.mikekuzn.mscheduler.domain.UseCasesInter
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class Signing @Inject constructor(
    @ActivityContext private val context: Context,
    private val useCases: UseCasesInter,
) : SigningInter {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var currentUser = firebaseAuth.currentUser

    override var waiting by mutableStateOf(true)
    override var signed by mutableStateOf(false)
    override var isEmailVerified by mutableStateOf(false)
    override var messageOrState by mutableStateOf<String?>(null)
    override val currentEmail get() = currentUser?.email ?: ""

    private val authStateListener = FirebaseAuth.AuthStateListener {
        readState()
        waiting = false
    }

    init {
        //firebaseAuth.signInWithCustomToken()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun unsubscribe() {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    private fun testString(email: String, pass: String): Boolean {
        messageOrState = null
        if (!email.isEmailValid()) {
            messageOrState = context.getString(R.string.incorrectEmail)
        } else if (pass.length < 5) {
            messageOrState = context.getString(R.string.incorrectPass)
        }
        return messageOrState == null
    }

    override fun signUp(email: String, pass: String) {
        if (testString(email, pass)) {
            firebaseAuth
                .createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    Log.d("***[", "signUp $it")
                    messageOrState = context.getString(R.string.signUpSuccessful)
                }
                .addOnFailureListener {
                    Log.d("***[", "signUp Failure $it")
                    messageOrState = context.getString(R.string.signUpError) + " " + it.message
                }
        }
    }

    override fun signIn(email: String, pass: String) {
        if (testString(email, pass)) {
            firebaseAuth
                .signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("***[", "signIn $it")
                        messageOrState = context.getString(R.string.signInSuccessful)
                    }
                }
                .addOnFailureListener {
                    Log.d("***[", "signIn Failure $it")
                    messageOrState = context.getString(R.string.signInError) + " " + it.message
                }
        }
    }

    override fun sendMailVerification() {
        currentUser?.sendEmailVerification()
    }

    override fun readState() {
        currentUser = firebaseAuth.currentUser
        val oldState = signed && isEmailVerified
        signed = currentUser != null
        isEmailVerified = currentUser?.isEmailVerified ?: false
        Log.d("***[", "readState $currentUser $isEmailVerified")
        if (!oldState && signed && isEmailVerified) {
            val addString = currentUser!!.uid
            useCases.setUserPath(addString)
        }
    }

    override fun signOut() {
        Log.d("***[", "signOut")
        useCases.clrUserPath()
        firebaseAuth.signOut()
    }

    /*override fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
        //firebaseAuth.confirmPasswordReset()
    }// */

    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}

val LocalSigning = compositionLocalOf<SigningInter> { error("Signing logic is not provided") }