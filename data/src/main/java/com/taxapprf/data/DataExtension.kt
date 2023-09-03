package com.taxapprf.data

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.taxapprf.data.error.DataErrorConnection
import com.taxapprf.data.error.DataErrorUser
import com.taxapprf.data.error.DataErrorUserEmailAlreadyUse
import com.taxapprf.data.error.DataErrorUserWrongPassword
import com.taxapprf.domain.Sync
import com.taxapprf.domain.transaction.TransactionType
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs
import kotlin.math.floor

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
            else -> e
        }
    }
}

fun Double.roundUpToTwo() = floor(this * 100.0) / 100.0

fun updateTax(sum: Double, type: String, rateCBR: Double): Double {
    var newSum = sum
    val k = when (type) {
        TransactionType.COMMISSION.name -> -1.0
        TransactionType.FUNDING_WITHDRAWAL.name -> {
            newSum = abs(sum)
            0.0
        }

        else -> 1.0
    }

    val newTax = newSum * rateCBR * 0.13 * k
    return newTax.roundUpToTwo()
}

fun <T : Sync> MutableMap<String, T>.sync(
    remoteList: List<T>,
    saveLocal: (List<T>) -> Unit,
    deleteLocal: (List<T>) -> Unit,
    saveRemote: (List<T>) -> Unit,
) {
    val cache = this
    val deleteLocalList = mutableListOf<T>()
    val saveLocalList = mutableListOf<T>()
    val saveRemoteList = mutableListOf<T>()

    remoteList.map {
        if (cache.isCached(it.key)) {
            if (cache.isNotSync(it.key)) {
                if (cache.isExpired(it)) saveLocalList.add(it)
                else saveRemoteList.add(it)
            }

            cache.remove(it.key)
        } else saveLocalList.add(it)
    }

    cache.map {
        if (it.value.isSync) deleteLocalList.add(it.value)
        else saveRemoteList.add(it.value)
    }

    if (saveLocalList.isNotEmpty()) saveLocal(saveLocalList)
    if (deleteLocalList.isNotEmpty()) deleteLocal(deleteLocalList)
    if (saveRemoteList.isNotEmpty()) saveRemote(saveRemoteList)
}

private fun <T : Sync> MutableMap<String, T>.isCached(key: String) =
    containsKey(key)

private fun <T : Sync> MutableMap<String, T>.isNotSync(key: String) =
    !getValue(key).isSync

private fun <T : Sync> MutableMap<String, T>.isExpired(sync: T) =
    getValue(sync.key).syncAt <= sync.syncAt

fun getTime() = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)