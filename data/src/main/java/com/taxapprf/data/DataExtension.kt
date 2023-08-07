package com.taxapprf.data

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.taxapprf.data.error.AuthError
import com.taxapprf.data.error.SignInErrorWrongPassword
import com.taxapprf.data.error.SignUpErrorEmailAlreadyUse
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.domain.transaction.SaveTransactionModel
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

inline fun <T> safeCall(call: () -> T): T {
    return try {
        call()
    } catch (e: Exception) {
        throw e.toAppError()
    }
}

fun Exception.toAppError() = when (this) {
    is FirebaseAuthUserCollisionException -> SignUpErrorEmailAlreadyUse()
    is FirebaseAuthInvalidCredentialsException -> SignInErrorWrongPassword()
    is FirebaseAuthInvalidUserException -> SignInErrorWrongPassword()
    is FirebaseException -> AuthError()
    else -> this
}

fun SaveTransactionModel.calculateTax() {
    val k = when (TransactionType.valueOf(type)) {
        TransactionType.TRADE -> 1
        TransactionType.FUNDING_WITHDRAWAL -> 0
        TransactionType.COMMISSION -> {
            sum = abs(sum)
            -1
        }
    }

    tax = (sum * rateCBR * 0.13 * k).toLong()
}