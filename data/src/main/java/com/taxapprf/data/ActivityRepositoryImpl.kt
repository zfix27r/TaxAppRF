package com.taxapprf.data

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.taxapprf.data.FirebaseAPI.Companion.getAsDouble
import com.taxapprf.data.FirebaseAPI.Companion.getAsString
import com.taxapprf.data.error.AuthErrorCurrentUserIsEmpty
import com.taxapprf.data.error.AuthErrorUndefined
import com.taxapprf.data.local.dao.AccountDao
import com.taxapprf.data.local.dao.TaxDao
import com.taxapprf.data.local.dao.UserDao
import com.taxapprf.data.local.entity.AccountEntity
import com.taxapprf.data.local.entity.TaxEntity
import com.taxapprf.data.local.entity.TransactionEntity
import com.taxapprf.data.local.entity.UserEntity
import com.taxapprf.data.local.model.UserWithAccountModel
import com.taxapprf.domain.ActivityRepository
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.UserModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val firebaseAPI: FirebaseAPI,
    private val userDao: UserDao,
    private val accountDao: AccountDao,
    private val taxDao: TaxDao
) : ActivityRepository {
    override fun getUser() = userDao.getSignIn().map { userWithAccountNull ->
        userWithAccountNull?.let { userWithAccount ->
            firebaseAPI.isSignIn()?.let { uid ->
                userWithAccount.accountName?.let { accountName ->
                    firebaseAPI.accountName = accountName
                }
            }
            userWithAccount.toUserModel()
        } ?: run { null }
    }

    private fun UserWithAccountModel.toUserModel() =
        UserModel(name, email, phone, accountName)

    private suspend fun restoreFirebaseData() {
        println("@@@@ start")

        accountDao.drop()
        taxDao.dropTaxes()
        taxDao.dropTransactions()

        println("@@@@ start 2")

        val accounts = mutableListOf<AccountEntity>()
        val taxes = mutableListOf<TaxEntity>()
        val transactions = mutableListOf<TransactionEntity>()

        println("@@@@ start 3")
        try {
            firebaseAPI.getAccounts()
                .mapNotNull { account ->
                    println("acc" + account)
                    account.key?.let { accountKey ->
                        accounts.add(AccountEntity(accountKey, false))
                        account.children.mapNotNull { year ->
                            year.key?.let { yearKey ->
                                var sumTaxes = 0.0

                                year.child(FirebaseAPI.TRANSACTIONS).children.mapNotNull { transaction ->
                                    transactions.add(transaction.getTransaction(yearKey, accountKey))
                                    sumTaxes += transactions.last().sumRub
                                }

                                taxes.add(TaxEntity(0, accountKey, yearKey, sumTaxes))
                            }
                        }
                    }
                }

        } catch (e: Exception) {
            println("sssss  " + e.message)
        }

        println("account " + accounts)
        println("taxes " + taxes)
        println("transactions" + transactions)
        if (accounts.isNotEmpty()) accountDao.saveAccounts(accounts)
        if (taxes.isNotEmpty()) taxDao.saveTaxes(taxes)
        if (transactions.isNotEmpty()) taxDao.saveTransactions(transactions)
    }

    override fun signIn(signInModel: SignInModel) = flow<Unit> {
        with(signInModel) {
            firebaseAPI.signIn(email, password)?.let { firebaseuser ->
                firebaseAPI.isSignIn()?.let {
                    if (accountDao.countAccount() == 0) {
                        println("@@@@ " + signInModel)
                        userDao.save(firebaseuser.toUserEntity())
                        restoreFirebaseData()
                    }
                }
                println(" dfdfd" + accountDao.countAccount())
            } ?: run { throw AuthErrorCurrentUserIsEmpty() }
        }
//        emit(Unit)
    }

    override fun signUp(signUpModel: SignUpModel) = flow {
        if (firebaseAPI.signUp(signUpModel)) {
            //userDao.save(signUpModel.toUserEntity())
            emit(Unit)
        } else throw AuthErrorUndefined()
    }

    override fun signOut() = flow {
        firebaseAPI.signOut()
        userDao.signOut()
        emit(Unit)
    }

    private fun FirebaseUser.toUserEntity() = UserEntity(
        key = uid,
        isSignIn = true,
        name = displayName ?: "",
        email = email ?: "",
        phone = phoneNumber ?: ""
    )

    //private fun SignUpModel.toUserEntity() = UserEntity(name, true, email, phone)

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