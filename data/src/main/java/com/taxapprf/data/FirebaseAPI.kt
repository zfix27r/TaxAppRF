package com.taxapprf.data

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.taxapprf.data.error.AuthErrorUndefined
import com.taxapprf.data.error.FirebaseErrorValueIsEmpty
import com.taxapprf.data.error.SignInErrorWrongPassword
import com.taxapprf.data.error.SignUpErrorEmailAlreadyUse
import com.taxapprf.data.error.UserErrorSessionExpire
import com.taxapprf.data.local.entity.UserEntity
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal
import javax.inject.Inject

class FirebaseAPI @Inject constructor() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.reference

    var uid = ""
    var accountName = ""
    var year = ""
    private val refUsers
        get() = reference.child(USERS)
    private val refUsersUid
        get() = refUsers.child(uid)
    private val refUsersUidAccounts
        get() = refUsersUid.child(ACCOUNTS)
    private val refUsersUidAccountsAid
        get() = refUsersUidAccounts.child(accountName)
    private val refUsersUidAccountAidYear
        get() = refUsersUidAccountsAid.child(year)
    private val refUsersUidAccountAidYearTransactions
        get() = refUsersUidAccountAidYear.child(TRANSACTIONS)
    private val refUsersUidAccountAidYearSum
        get() = refUsersUidAccountAidYear.child(KEY_ACCOUNTS_SUM_TAXES)

    fun isSignIn() =
        auth.currentUser?.let {
            uid = it.uid
            uid
        } ?: run { null }

    suspend fun signIn(email: String, password: String) =
        safeCallWithoutAuth {
            auth
                .signInWithEmailAndPassword(email, password)
                .await()
                .user
        }

    suspend fun signUp(signUpModel: SignUpModel): Boolean = safeCallWithoutAuth {
        with(signUpModel) {
            auth
                .createUserWithEmailAndPassword(email, password)
                .await()
                .user?.let {
                    refUsersUid
                        .setValue(signUpModel)
                        .await()
                    return true
                }
        }
        throw AuthErrorUndefined()
    }

    fun signOut(): Unit = safeCall {
        auth.signOut()
        return
    }


    suspend fun getAccounts() =
        safeCall {
            reference
                .child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .get()
                .await()
                .children
        }

    suspend fun getTransaction(transactionKey: String) =
        safeCall {
            refUsersUidAccountAidYearTransactions
                .child(transactionKey)
                .get()
                .await()
                .getValue(TransactionModel::class.java)!! // TODO доделать перехват ошибки

            // TODO здесь так же было сохранение суммы
        }

    suspend fun getTransactions(year: String) =
        safeCall {
            this.year = year
            refUsersUidAccountAidYearTransactions
                .get()
                .await()
                .children
                .mapNotNull { it.getTransactionModel() }
        }

    suspend fun saveTransaction(transaction: SaveTransactionModel) =
        safeCall {
            transaction.key?.let {
                transaction.updateFirebaseTransaction()
            } ?: run {
                transaction.addFirebaseTransaction()
                Unit//transaction.updateFirebaseYear()
            }
        }

    suspend fun deleteTransaction(request: FirebaseRequestModel): Unit =
        safeCall {
            with(request) {
                refUsersUidAccountAidYearTransactions
                    .child(key)
                    .setValue(null)
                    .await()
            }
        }

    suspend fun getYearSum(firebaseModel: FirebaseRequestModel): Double =
        safeCall {
            with(firebaseModel) {
                refUsersUidAccountAidYearSum
                    .get()
                    .await()
                    ?.let { it.value as Double }
                    ?: run { 0.0 }
            }
        }

    suspend fun sumTaxes() = safeCall {
/*        var currentYearSumBigDecimal = BigDecimal(0)

        refUsersUidAccountAidYearTransactions
            .get()
            .await()
            .children
            .mapNotNull {ds ->
                val transaction: Transaction? = ds.getValue(Transaction::class.java)
                currentYearSumBigDecimal =
                    currentYearSumBigDecimal.add(BigDecimal.valueOf(transaction.sumRub))
            }

        saveYearSum()*/

    }

    suspend fun getTaxes(accountName: String) = safeCall { uid ->
        reference
            .child(USERS)
            .child(uid)
            .child(ACCOUNTS)
            .child(accountName)
            .get()
            .await()
            .children
    }

    suspend fun saveYearSum(saveYearSumModel: SaveYearSumModel) {
        safeCall {
            with(saveYearSumModel) {
                val firebaseModel = FirebaseRequestModel(account, year)
                val oldYearSum = getYearSum(firebaseModel)
                val bigYearSum = BigDecimal(oldYearSum)
                bigYearSum.add(BigDecimal(yearSum))

                refUsersUidAccountAidYearSum
                    .setValue(bigYearSum.toDouble())
                    .await()
            }
        }
    }

    suspend fun deleteYearSum(request: FirebaseRequestModel): Unit =
        safeCall {
            with(request) {
                refUsersUidAccountAidYear
                    .child(key)
                    .setValue(null)
                    .await()
            }
        }

    private suspend fun SaveTransactionModel.updateFirebaseTransaction() {
        refUsersUidAccountAidYearTransactions
            .child(key!!)
            .setValue(this)
            .await()
    }

    private suspend fun SaveTransactionModel.addFirebaseTransaction() =
        safeCall { uid ->
            println("@@@" + this)
            val ref = reference.child(USERS)
                .child(uid)
                .child(ACCOUNTS)
                .child(accountName)
                .child(year)
                .child(TRANSACTIONS)

            println("@@@@@ " + ref)

            ref.push().key?.let { key ->
                println("!!!!! " + key)
                val ggg = this.toHashMap(key)
                println("!!! " + ggg)
                ref
                    .child(key)
                    .setValue(ggg)
                    .await()
            }// ?: run { throw FirebaseErrorKeyIsEmpty() }
        }

    private suspend fun SaveTransactionModel.updateFirebaseYear() {
        refUsersUidAccountAidYear
            .setValue(year)
            .await()
    }

    private inline fun <T> safeCallWithoutAuth(call: () -> T): T {
        return try {
            call()
        } catch (e: Exception) {
            throw e.getAppError()
        }
    }

    private inline fun <T> safeCall(call: (uid: String) -> T): T {
        return try {
            val uid = auth.uid ?: throw UserErrorSessionExpire()
            call(uid)
        } catch (e: Exception) {
            throw e.getAppError()
        }
    }

    private fun Exception.getAppError() = when (this) {
        is FirebaseAuthUserCollisionException -> SignUpErrorEmailAlreadyUse()
        is FirebaseAuthInvalidCredentialsException -> SignInErrorWrongPassword()
        is FirebaseAuthInvalidUserException -> SignInErrorWrongPassword()
        is FirebaseException -> AuthErrorUndefined()
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
            child(path).getValue(String::class.java)
                ?: throw FirebaseErrorValueIsEmpty()

        fun DataSnapshot.getAsDouble(path: String) =
            child(path).getValue(Double::class.java)
                ?: throw FirebaseErrorValueIsEmpty()

    }
}