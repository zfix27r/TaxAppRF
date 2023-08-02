package com.taxapprf.data

import com.google.firebase.database.DataSnapshot
import com.taxapprf.data.FirebaseAPI.Companion.getAsDouble
import com.taxapprf.data.FirebaseAPI.Companion.getAsString
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
import com.taxapprf.domain.user.AccountModel
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
    override fun getUser() = userDao.getSignIn().map { userWithAccount ->
        println("@@@@ 1" + userWithAccount)
        //TODO firebaseAPI.isSignIn() - только потому что нет возможности протестировать выход
        firebaseAPI.isSignIn()?.let { uid ->
            println("@@@@ 2")
            userWithAccount.accountName?.let { accountName ->
                println("@@@@ 3")
                firebaseAPI.accountName = accountName

                UserModel(
                    name = userWithAccount.name,
                    email = userWithAccount.email,
                    phone = userWithAccount.phone,
                    account = accountName
                )
            } ?: run {
                println("@@@@ 4")
                restoreFirebaseData(userWithAccount.name)
                null
            }
        }
    }

    private suspend fun restoreFirebaseData(userName: String) {
//        accountDao.drop()
//        taxDao.dropTaxes()
//        taxDao.dropTransactions()

        val accounts = mutableListOf<AccountEntity>()
        val taxes = mutableListOf<TaxEntity>()
        val transactions = mutableListOf<TransactionEntity>()

        firebaseAPI.getAccounts()
            .mapNotNull { account ->
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

        println(accounts)
        println(taxes)
        println(transactions)
        if (accounts.isNotEmpty()) accountDao.saveAccounts(accounts)
        if (taxes.isNotEmpty()) taxDao.saveTaxes(taxes)
        if (transactions.isNotEmpty()) taxDao.saveTransactions(transactions)
    }

    override fun signIn(signInModel: SignInModel) = flow {
        with(signInModel) {
            firebaseAPI.signIn(email, password)
            userDao.save(firebaseAPI.getUserEntity())
            // TODO как нибудь потом отделить класс фаербейза от приложения
        }
        emit(Unit)
    }

    override fun signUp(signUpModel: SignUpModel) = flow {
        if (firebaseAPI.signUp(signUpModel)) {
            userDao.save(signUpModel.toUserEntity())
            emit(Unit)
        } else throw AuthErrorUndefined()
    }

    override fun signOut() = flow {
        firebaseAPI.signOut()
        userDao.signOut()
        emit(Unit)
    }

    private fun SignUpModel.toUserEntity() = UserEntity(name, true, email, phone)

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