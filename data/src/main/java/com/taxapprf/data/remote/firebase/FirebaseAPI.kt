package com.taxapprf.data.remote.firebase

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.taxapprf.data.error.AuthError
import com.taxapprf.data.error.AuthErrorSessionExpired
import com.taxapprf.data.error.SignInErrorWrongPassword
import com.taxapprf.data.error.SignUpErrorEmailAlreadyUse
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAPI @Inject constructor() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.reference

    fun isSignIn() = auth.currentUser?.uid

    suspend fun signIn(email: String, password: String) =
        safeCall {
            auth.signInWithEmailAndPassword(email, password)
                .await()
                .user
        }

    suspend fun signUpWithEmailAndPassword(email: String, password: String) =
        safeCall {
            auth.createUserWithEmailAndPassword(email, password)
                .await()
                .user
        }

    fun signOut() =
        safeAuthCall {
            auth.signOut()
        }

    suspend fun saveProfile(signUpModel: SignUpModel): Unit =
        safeAuthCall { uid ->
            reference
                .child(USERS)
                .child(uid)
                .setValue(signUpModel)
                .await()
        }

    suspend fun getAccounts(): MutableIterable<DataSnapshot> =
        safeAuthCall { uid ->
            reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .get()
                .await()
                .children
        }

    suspend fun getTransaction(request: FirebaseRequestModel) =
        safeAuthCall { uid ->
            reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .child(request.account)
                .child(request.year)
                .child(TRANSACTIONS)
                .child(request.transactionKey)
                .get()
                .await()
                .getValue(TransactionModel::class.java) ?: throw AuthError()
        }

    suspend fun getTransactions(request: FirebaseRequestModel) =
        safeAuthCall { uid ->
            reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .child(request.account)
                .child(request.year)
                .child(TRANSACTIONS)
                .get()
                .await()
                .children
                .mapNotNull { it.getTransactionModel() }
        }

    suspend fun updateTransaction(transaction: SaveTransactionModel): String =
        safeAuthCall { uid ->
            val key = transaction.key ?: throw AuthError()

            reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .child(transaction.account)
                .child(transaction.year)
                .child(TRANSACTIONS)
                .child(key)
                .setValue(this)
                .await()

            return key
        }

    suspend fun addTransaction(transaction: SaveTransactionModel): String =
        safeAuthCall { uid ->
            val transactionReference = reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .child(transaction.account)
                .child(transaction.year)
                .child(TRANSACTIONS)

            val key = transactionReference.push().key ?: throw AuthError()
            transaction.key = key

            transactionReference
                .child(key)
                .setValue(transaction.toHashMap(key))
                .await()

            return key
        }

    suspend fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel): Unit =
        safeAuthCall { uid ->
            with(deleteTransactionModel) {
                reference
                    .child(USERS)
                    .child(uid)
                    .child(ACCOUNTS)
                    .child(account)
                    .child(year)
                    .child(TRANSACTIONS)
                    .child(transactionKey)
                    .setValue(null)
                    .await()
            }
        }

    suspend fun getYearSum(request: FirebaseRequestModel): Double =
        safeAuthCall { uid ->
            reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .child(request.account)
                .child(request.year)
                .child(KEY_ACCOUNTS_SUM_TAXES)
                .get()
                .await()
                ?.let { it.value as Double }
                ?: run { 0.0 }
        }

    suspend fun getTaxes(request: FirebaseRequestModel): MutableIterable<DataSnapshot> =
        safeAuthCall { uid ->
            reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .child(request.account)
                .get()
                .await()
                .children
        }

    suspend fun deleteTax(request: FirebaseRequestModel): Unit =
        safeAuthCall { uid ->
            reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .child(request.account)
                .child(request.year)
                .setValue(null)
                .await()
        }

    suspend fun saveYearSum(
        request: FirebaseRequestModel,
        saveYearSumModel: SaveYearSumModel
    ) =
        safeAuthCall { uid ->
/*
            val oldYearSum = getYearSum(firebaseModel)
            val bigYearSum = BigDecimal(oldYearSum)
            bigYearSum.add(BigDecimal(yearSum))

            refUsersUidAccountAidYearSum
                .setValue(bigYearSum.toDouble())
                .await()

            reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .child(request.account)
                .child(request.year)
                .child(KEY_ACCOUNTS_SUM_TAXES)
*/
        }

    suspend fun deleteYearSum(request: FirebaseRequestModel): Unit =
        safeAuthCall { uid ->
/*            reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .child(request.account)
                .child(request.year)
                .child(TRANSACTIONS)
                .child(request.transactionKey)
                .setValue(null)
                .await()*/
        }


    private suspend fun SaveTransactionModel.updateFirebaseYear() {
        /*        refUsersUidAccountAidYear
                    .setValue(year)
                    .await()*/
    }


    private fun Exception.toAppError() = when (this) {
        is FirebaseAuthUserCollisionException -> SignUpErrorEmailAlreadyUse()
        is FirebaseAuthInvalidCredentialsException -> SignInErrorWrongPassword()
        is FirebaseAuthInvalidUserException -> SignInErrorWrongPassword()
        is FirebaseException -> AuthError()
        else -> this
    }

    private fun DataSnapshot.getTransactionModel() = TransactionModel(
        key = getAsString(KEY_TRANSACTION_KEY),
        type = getAsString(KEY_TRANSACTION_TYPE),
        id = getAsString(KEY_TRANSACTION_ID),
        date = getAsString(KEY_TRANSACTION_DATE),
        currency = getAsString(KEY_TRANSACTION_CURRENCY),
        rateCentralBank = getAsDouble(KEY_TRANSACTION_RATE_CENTRAL_BANK),
        sum = getAsDouble(KEY_TRANSACTION_SUM),
        sumRub = getAsDouble(KEY_TRANSACTION_SUM_RUB),
    )

    private fun SaveTransactionModel.toHashMap(key: String) = hashMapOf(
        KEY_TRANSACTION_KEY to key,
        KEY_TRANSACTION_ID to id,
        KEY_TRANSACTION_DATE to date,
        KEY_TRANSACTION_TYPE to type,
        KEY_TRANSACTION_SUM to sum,
        KEY_TRANSACTION_CURRENCY to currency,
        KEY_TRANSACTION_RATE_CENTRAL_BANK to rateCentralBank,
        KEY_TRANSACTION_SUM_RUB to sumRub,
    )

    private inline fun <T> safeCall(call: () -> T): T {
        return try {
            call()
        } catch (e: Exception) {
            throw e.toAppError()
        }
    }

    private inline fun <T> safeAuthCall(call: (uid: String) -> T): T {
        return try {
            val uid = auth.uid ?: throw AuthErrorSessionExpired()
            call(uid)
        } catch (e: Exception) {
            throw e.toAppError()
        }
    }

    companion object {
        const val USERS = "Users"
        const val KEY_USER_NAME = "name"
        const val KEY_USER_EMAIL = "email"
        const val KEY_USER_PHONE = "phone"

        const val ACCOUNTS = "accounts"
        const val KEY_ACCOUNTS_SUM_TAXES = "sumTaxes"
        const val KEY_ACCOUNTS_YEAR = "year"

        const val TRANSACTIONS = "transactions"
        const val KEY_TRANSACTION_KEY = "key"
        const val KEY_TRANSACTION_ID = "id"
        const val KEY_TRANSACTION_DATE = "date"
        const val KEY_TRANSACTION_TYPE = "type"
        const val KEY_TRANSACTION_SUM = "sum"
        const val KEY_TRANSACTION_CURRENCY = "currency"
        const val KEY_TRANSACTION_RATE_CENTRAL_BANK = "rateCentralBank"
        const val KEY_TRANSACTION_SUM_RUB = "sumRub"


        const val DEFAULT_ACCOUNT = "DefaultAccount"

        fun DataSnapshot.getAsString(path: String) =
            child(path).getValue(String::class.java) ?: throw AuthError()

        fun DataSnapshot.getAsDouble(path: String) =
            child(path).getValue(Double::class.java) ?: throw AuthError()
    }
}