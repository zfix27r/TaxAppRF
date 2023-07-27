package com.taxapprf.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.taxapprf.data.error.SignInErrorWrongPassword
import com.taxapprf.data.error.SignUpErrorEmailAlreadyUse
import com.taxapprf.data.local.dao.ActivityDao
import com.taxapprf.domain.ActivityRepository
import com.taxapprf.domain.transaction.GetTransactionModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.SignInModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal
import javax.inject.Inject


class ActivityRepositoryImpl @Inject constructor(
    private val dao: ActivityDao
) : ActivityRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.reference

    override fun isSignIn() = auth.currentUser != null

    override fun signIn(signInModel: SignInModel) = flow {
        with(signInModel) {
            safeCall {
                auth.signInWithEmailAndPassword(email, password).await()
                emit(Unit)
            }
        }
    }

    override fun signUp(userSignUpModel: SignUpModel) = flow {
        with(userSignUpModel) {
            safeCall {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                authResult.user?.let {
                    reference
                        .child(PATH_USERS)
                        .child(it.uid)
                        .setValue(userSignUpModel)
                        .await()

                    emit(Unit)
                }
            }
        }
    }

    override fun getAccounts() = flow {
        auth.currentUser?.let { user ->
            safeCall {
                val accountsSnapshot = Firebase.database.reference
                    .child(PATH_USERS)
                    .child(user.uid)
                    .child(PATH_ACCOUNTS)
                    .get()
                    .await()

                emit(accountsSnapshot.children.filter { it.key != null }.map { it.key!! })
            }
        }
    }

    override fun signOut() = flow {
        auth.signOut()
        emit(Unit)
    }

    override fun setActiveAccount(accountName: String) = flow {
        val request = dao.save(accountName)
        if (request == 1) emit(Unit)
        else throw Exception()
    }

    fun getTransactions(year: String) = flow {
        auth.currentUser?.let { user ->
            safeCall {
                val referenceToYear = reference
                    .child(PATH_USERS)
                    .child(user.uid)
                    .child(PATH_ACCOUNTS)
                    .child(year)

                val transactions = referenceToYear
                    .child(PATH_TRANSACTIONS)
                    .get()
                    .await()
                    .children
                    .mapNotNull { it.getValue(GetTransactionModel::class.java) }

                if (transactions.isNotEmpty()) {
                    //TODO("почему? зачем это сохранение")
                    val sum = BigDecimal(0)
                    transactions.map { sum.add(BigDecimal.valueOf(it.sumRub)) }
                    referenceToYear
                        .child(PATH_SUM_TAXES)
                        .setValue(sum)
                }

                emit(transactions)
            }
        }
    }

    suspend fun getYearSum(year: String) {
        auth.currentUser?.let { user ->
            safeCall {
                val snapshot = reference
                    .child(PATH_USERS)
                    .child(user.uid)
                    .child(PATH_ACCOUNTS)
                    .child(year)
                    .child(PATH_SUM_TAXES)
                    .get()
                    .await()

                var sumTaxes = 0.0
                snapshot?.let {
                    sumTaxes = snapshot.value as Double
                }
                sumTaxes
            }
        }
    }

    private inline fun <T> safeCall(call: () -> T): T {
        return try {
            call()
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
    }
}