package com.taxapprf.data

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.taxapprf.data.error.AuthError
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.getAsDouble
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.getAsString
import com.taxapprf.data.local.room.dao.AccountDao
import com.taxapprf.data.local.room.dao.TaxDao
import com.taxapprf.data.local.room.dao.TransactionDao
import com.taxapprf.data.local.room.dao.UserDao
import com.taxapprf.data.local.room.entity.AccountEntity
import com.taxapprf.data.local.room.entity.TaxEntity
import com.taxapprf.data.local.room.entity.TransactionEntity
import com.taxapprf.data.local.room.entity.UserEntity
import com.taxapprf.data.local.room.model.UserWithAccountDataModel
import com.taxapprf.data.remote.firebase.FirebaseAPI
import com.taxapprf.domain.SignRepository
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.UserModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAPI: FirebaseAPI,
    private val userDao: UserDao,
    private val accountDao: AccountDao,
    private val taxDao: TaxDao,
    private val transactionDao: TransactionDao,
) : SignRepository {
    override fun getUser() = userDao.getUserWithAccount()
        .map { _userWithAccount ->
            _userWithAccount
                ?.let { userWithAccount ->
                    firebaseAPI.isSignIn()
                        ?.let { userWithAccount.toUserModel() }
                        ?: run { null }
                } ?: run { null }
        }

    private fun UserWithAccountDataModel.toUserModel() =
        UserModel(name, email, phone, account)

    override fun signIn(signInModel: SignInModel) = flow {
        with(signInModel) {
            firebaseAPI.signIn(email, password)
                ?.let { user ->
                    firebaseAPI.isSignIn()
                        ?.let {
                            if (userDao.isUserLocalAuth(email) == 0) restoreFirebaseData()
                            userDao.save(user.toUserEntity())
                        } ?: run { throw AuthError() }
                } ?: run { throw AuthError() }
        }
        emit(Unit)
    }

    override fun signUp(signUpModel: SignUpModel) = flow {
        with(signUpModel) {
            firebaseAPI.signUpWithEmailAndPassword(email, password)
                ?.let { user ->
                    firebaseAPI.isSignIn()
                        ?.let {
                            if (userDao.isUserLocalAuth(email) == 0) restoreFirebaseData()
                            firebaseAPI.saveProfile(signUpModel)
                            userDao.save(user.toUserEntity())
                        } ?: run { throw AuthError() }
                } ?: run { throw AuthError() }
        }
        emit(Unit)
    }

    override fun signOut() = flow {
        firebaseAPI.signOut()
        userDao.signOut()
        emit(Unit)
    }

    private suspend fun restoreFirebaseData() {
        accountDao.drop()
        taxDao.dropTaxes()
        transactionDao.dropTransactions()

        val accounts = mutableListOf<AccountEntity>()
        val taxes = mutableListOf<TaxEntity>()
        val transactions = mutableListOf<TransactionEntity>()

        firebaseAPI.getAccounts()
            .mapNotNull { account ->
                account.key
                    ?.let { key ->
                        accounts.add(AccountEntity(key, false))
                        account.children
                            .mapNotNull { year ->
                                year.key
                                    ?.let { yearKey ->
                                        var sumTaxes = 0.0

                                        year.child(FirebaseAPI.TRANSACTIONS).children
                                            .mapNotNull { transaction ->
                                                transactions.add(
                                                    transaction.getTransaction(yearKey, key)
                                                )
                                                sumTaxes += transactions.last().sumRub
                                            }
                                        taxes.add(TaxEntity(0, key, yearKey, sumTaxes))
                                    }
                            }
                    }
            }

        if (accounts.isNotEmpty()) accountDao.saveAccounts(accounts)
        if (taxes.isNotEmpty()) taxDao.saveTaxes(taxes)
        if (transactions.isNotEmpty()) transactionDao.saveTransactions(transactions)
    }

    private fun FirebaseUser.toUserEntity() = UserEntity(
        key = uid,
        isSignIn = true,
        name = displayName ?: "",
        email = email ?: "",
        phone = phoneNumber ?: ""
    )

    private fun DataSnapshot.getTransaction(year: String, account: String) =
        TransactionEntity(
            key = getAsString(FirebaseAPI.KEY_TRANSACTION_KEY),
            account = account,
            year = year,
            type = getAsString(FirebaseAPI.KEY_TRANSACTION_TYPE),
            id = getAsString(FirebaseAPI.KEY_TRANSACTION_ID),
            date = getAsString(FirebaseAPI.KEY_TRANSACTION_DATE),
            currency = getAsString(FirebaseAPI.KEY_TRANSACTION_CURRENCY),
            rateCentralBank = getAsDouble(FirebaseAPI.KEY_TRANSACTION_RATE_CENTRAL_BANK),
            sum = getAsDouble(FirebaseAPI.KEY_TRANSACTION_SUM),
            sumRub = getAsDouble(FirebaseAPI.KEY_TRANSACTION_SUM_RUB),
        )
}