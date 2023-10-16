package com.taxapprf.data

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseException
import com.taxapprf.data.error.DataErrorConnection
import com.taxapprf.data.error.DataErrorExternal
import com.taxapprf.data.error.DataErrorUser
import com.taxapprf.data.error.DataErrorUserEmailAlreadyUse
import com.taxapprf.data.error.DataErrorUserWrongPassword
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset


inline fun <T> safeCall(call: () -> T): T {
    return try {
        call()
    } catch (e: Exception) {
        throw when (e) {
            is FirebaseAuthUserCollisionException -> DataErrorUserEmailAlreadyUse()
            is FirebaseAuthInvalidCredentialsException -> DataErrorUserWrongPassword()
            is FirebaseAuthInvalidUserException -> DataErrorUserWrongPassword()
            is FirebaseException -> DataErrorUser()
            is SocketTimeoutException -> DataErrorConnection()
            is DatabaseException -> DataErrorExternal()
            else -> e
        }
    }
}

fun getEpochDate() = LocalDate.now().toEpochDay()
fun getEpochTime() = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
fun Long.getYear() = LocalDate.ofEpochDay(this).year.toString()