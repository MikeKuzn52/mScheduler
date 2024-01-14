package com.mikekuzn.mscheduler

interface SigningInter {
    val waiting: Boolean
    val signed: Boolean
    val isEmailVerified: Boolean
    val messageOrState: String?
    val currentEmail: String

    fun unsubscribe()
    fun signUp(email: String, pass: String)
    fun signIn(email: String, pass: String)
    fun sendMailVerification()
    fun readState()
    fun signOut()
    //fun resetPassword(email: String)
}