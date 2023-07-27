package com.taxapprf.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.taxapprf.data.error.SignInErrorWrongPassword
import com.taxapprf.data.error.SignUpErrorEmailAlreadyUse
import com.taxapprf.data.error.TransactionErrorNotGetNewKey
import com.taxapprf.data.error.UserErrorSessionExpire
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.transaction.GetTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal

class FirebaseAPI {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.reference

    suspend fun getTransaction(request: FirebaseRequestModel) =
        safeCall { user ->
            with(request) {
                reference
                    .toTransactionPath(user.uid, account, year)
                    .child(key)
                    .get()
                    .await()
                    .getValue(GetTransactionModel::class.java)!! // TODO доделать перехват ошибки

                // TODO здесь так же было сохранение суммы
            }
        }

    suspend fun saveTransaction(transaction: SaveTransactionModel) =
        safeCall { user ->
            transaction.key?.let {
                transaction.updateFirebaseTransaction(user.uid)
            } ?: run {
                transaction.addFirebaseTransaction(user.uid)
                transaction.updateFirebaseYear(user.uid)
            }
        }

    suspend fun deleteTransaction(request: FirebaseRequestModel): Unit =
        safeCall { user ->
            with(request) {
                reference
                    .toTransactionPath(user.uid, account, year)
                    .child(key)
                    .setValue(null)
                    .await()
            }
        }

    suspend fun getYearSum(firebaseModel: FirebaseRequestModel): Double =
        safeCall { user ->
            with(firebaseModel) {
                reference
                    .toSumTaxesPath(user.uid, account, year)
                    .get()
                    .await()
                    ?.let { it.value as Double }
                    ?: run { 0.0 }
            }
        }

    suspend fun saveYearSum(saveYearSumModel: SaveYearSumModel) {
        safeCall { user ->
            with(saveYearSumModel) {
                val firebaseModel = FirebaseRequestModel(account, year)
                val oldYearSum = getYearSum(firebaseModel)
                val bigYearSum = BigDecimal(oldYearSum)
                bigYearSum.add(BigDecimal(yearSum))

                reference
                    .toSumTaxesPath(user.uid, account, year)
                    .setValue(bigYearSum.toDouble())
                    .await()
            }
        }
    }

    suspend fun deleteYearSum(request: FirebaseRequestModel): Unit =
        safeCall { user ->
            with(request) {
                reference
                    .toYear(user.uid, account, year)
                    .child(key)
                    .setValue(null)
                    .await()
            }
        }

    private suspend fun SaveTransactionModel.updateFirebaseTransaction(uid: String) {
        reference
            .toTransactionPath(uid, account, year)
            .child(key!!)
            .setValue(this)
            .await()
    }

    private suspend fun SaveTransactionModel.addFirebaseTransaction(uid: String) {
        val path = reference.toTransactionPath(uid, account, year)

        path.push().key?.let { key ->
            this.key = key
            path.child(key)
                .setValue(this)
                .await()
        } ?: run { throw TransactionErrorNotGetNewKey() }
    }

    private suspend fun SaveTransactionModel.updateFirebaseYear(uid: String) {
        reference
            .toYearPath(uid, account, year)
            .setValue(year)
            .await()
    }

    private fun DatabaseReference.toYear(uid: String, account: String, year: String) = this
        .child(PATH_USERS)
        .child(uid)
        .child(PATH_ACCOUNTS)
        .child(account)
        .child(year)

    private fun DatabaseReference.toYearPath(uid: String, account: String, year: String) = this
        .child(PATH_USERS)
        .child(uid)
        .child(PATH_ACCOUNTS)
        .child(account)
        .child(year)
        .child(PATH_YEAR)

    private fun DatabaseReference.toTransactionPath(uid: String, account: String, year: String) =
        this
            .child(PATH_USERS)
            .child(uid)
            .child(PATH_ACCOUNTS)
            .child(account)
            .child(year)
            .child(PATH_TRANSACTIONS)

    private fun DatabaseReference.toSumTaxesPath(uid: String, account: String, year: String) = this
        .child(PATH_USERS)
        .child(uid)
        .child(PATH_ACCOUNTS)
        .child(account)
        .child(year)
        .child(PATH_SUM_TAXES)

    private inline fun <T> safeCall(call: (FirebaseUser) -> T): T {
        return try {
            auth.currentUser?.let { call(it) } ?: run { throw UserErrorSessionExpire() }
        } catch (e: Exception) {
            throw when (e) {
                is FirebaseAuthUserCollisionException -> SignUpErrorEmailAlreadyUse()
                is FirebaseAuthInvalidCredentialsException -> SignInErrorWrongPassword()
                is FirebaseAuthInvalidUserException -> SignInErrorWrongPassword()
                else -> e
            }
        }
    }

    companion object {
        const val PATH_USERS = "Users"
        const val PATH_ACCOUNTS = "accounts"
        const val PATH_TRANSACTIONS = "transactions"
        const val PATH_SUM_TAXES = "sumTaxes"
        const val PATH_YEAR = "year"
    }
}