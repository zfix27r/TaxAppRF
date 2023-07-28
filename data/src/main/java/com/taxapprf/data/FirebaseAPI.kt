package com.taxapprf.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.taxapprf.data.error.SignInErrorWrongPassword
import com.taxapprf.data.error.SignUpErrorEmailAlreadyUse
import com.taxapprf.data.error.TransactionErrorNotGetNewKey
import com.taxapprf.data.error.UserErrorSessionExpire
import com.taxapprf.data.local.dao.AccountDao
import com.taxapprf.data.local.model.FirebaseAccountModel
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.Transaction
import com.taxapprf.domain.taxes.TaxesAdapterModel
import com.taxapprf.domain.transaction.GetTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal
import javax.inject.Inject

class FirebaseAPI @Inject constructor(
    private val dao: AccountDao,
) {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.reference
    private var uid = ""
    private var accountId = ""
    private var year = ""
    private val refUsers
        get() = reference.child(PATH_USERS)
    private val refUsersUid
        get() = refUsers.child(uid)
    private val refUsersUidAccount
        get() = refUsersUid.child(PATH_ACCOUNTS)
    private val refUsersUidAccountAid
        get() = refUsersUidAccount.child(accountId)
    private val refUsersUidAccountAidYear
        get() = refUsersUidAccountAid.child(year)
    private val refUsersUidAccountAidYearTransactions
        get() = refUsersUidAccountAidYear.child(PATH_TRANSACTIONS)
    private val refUsersUidAccountAidYearSum
        get() = refUsersUidAccountAidYear.child(PATH_SUM_TAXES)

    fun isSignIn(): Boolean {
        auth.currentUser?.let {
            uid = it.uid
            runBlocking {
                launch(Dispatchers.IO) {
                    accountId = dao.getActiveAccountKey() ?: DEFAULT_ACCOUNT
                }
            }
            return true
        }
        return false
    }

    suspend fun signIn(signInModel: SignInModel): Unit = safeCallWithoutAuth {
        with(signInModel) {
            auth.signInWithEmailAndPassword(email, password).await()
        }
        return
    }

    suspend fun signUp(signUpModel: SignUpModel): Unit = safeCallWithoutAuth {
        with(signUpModel) {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                refUsersUid.setValue(signUpModel).await()
            }
        }
        return
    }


    fun signOut(): Unit = safeCall {
        auth.signOut()
        return
    }

    suspend fun getAccounts() = safeCall {
        refUsersUidAccount
            .get()
            .await()
            .children
            .mapNotNull { ds -> ds.key?.let { FirebaseAccountModel(it) } }
    }

    suspend fun getTransaction(request: FirebaseRequestModel) =
        safeCall {
            with(request) {
                refUsersUidAccountAidYearTransactions
                    .child(key)
                    .get()
                    .await()
                    .getValue(GetTransactionModel::class.java)!! // TODO доделать перехват ошибки

                // TODO здесь так же было сохранение суммы
            }
        }

    suspend fun saveTransaction(transaction: SaveTransactionModel) =
        safeCall {
            transaction.key?.let {
                transaction.updateFirebaseTransaction()
            } ?: run {
                transaction.addFirebaseTransaction()
                transaction.updateFirebaseYear()
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

    suspend fun getTaxes() = safeCall {
        refUsersUidAccountAidYear
            .get()
            .await()
            .children
            .mapNotNull { ds ->
                ds.key?.let {
                    TaxesAdapterModel(
                        year = it,
                        sum = ds.child(KEY_YEAR_SUM_TAXES).value.toString()
                    )
                }
            }
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

    private suspend fun SaveTransactionModel.addFirebaseTransaction() {
        refUsersUidAccountAidYearTransactions.push().key?.let { key ->
            this.key = key
            refUsersUidAccountAidYearTransactions.child(key)
                .setValue(this)
                .await()
        } ?: run { throw TransactionErrorNotGetNewKey() }
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

    private inline fun <T> safeCall(call: () -> T): T {
        return try {
            if (auth.currentUser == null) throw UserErrorSessionExpire()
            call()
        } catch (e: Exception) {
            throw e.getAppError()
        }
    }

    private fun Exception.getAppError() = when (this) {
        is FirebaseAuthUserCollisionException -> SignUpErrorEmailAlreadyUse()
        is FirebaseAuthInvalidCredentialsException -> SignInErrorWrongPassword()
        is FirebaseAuthInvalidUserException -> SignInErrorWrongPassword()
        else -> this
    }

    companion object {
        const val PATH_USERS = "Users"
        const val PATH_ACCOUNTS = "accounts"
        const val KEY_YEAR_SUM_TAXES = "sumTaxes"

        const val PATH_TRANSACTIONS = "transactions"
        const val PATH_SUM_TAXES = "sumTaxes"
        const val PATH_YEAR = "year"

        const val DEFAULT_ACCOUNT = "DefaultAccount"
    }
}