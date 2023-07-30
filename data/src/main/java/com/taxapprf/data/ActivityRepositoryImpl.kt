package com.taxapprf.data

import com.taxapprf.data.error.AuthErrorAccountEmpty
import com.taxapprf.data.error.FirebaseErrorUndefined
import com.taxapprf.data.local.dao.AccountDao
import com.taxapprf.data.local.dao.UserDao
import com.taxapprf.data.local.entity.UserEntity
import com.taxapprf.domain.ActivityRepository
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val firebaseAPI: FirebaseAPI,
    private val userDao: UserDao,
    private val dao: AccountDao
) : ActivityRepository {
    override fun isSignIn() = firebaseAPI.isSignIn()

    override fun signIn(signInModel: SignInModel) = flow {
        firebaseAPI.signIn(signInModel)
        dao.getActiveAccount()
            ?.let { firebaseAPI.accountKey = it }
            ?: run { userDao.save(firebaseAPI.getUserEntity()) }
        emit(Unit)
    }

    override fun signUp(signUpModel: SignUpModel) = flow {
        if (firebaseAPI.signUp(signUpModel)) {
            userDao.save(signUpModel.toUserEntity())
            emit(Unit)
        } else throw FirebaseErrorUndefined()
    }

    private fun SignUpModel.toUserEntity() = UserEntity(0, true, name, email, phone)

    override fun signOut() = flow {
        emit(firebaseAPI.signOut())
    }

    override fun getAccounts() = flow<List<String>> {
        firebaseAPI.getAccounts().ifEmpty { throw AuthErrorAccountEmpty() }
    }

    override fun saveAccount(accountName: String?) = flow {
        accountName?.let { name ->
            dao.getAccount(name)?.let {
                firebaseAPI.accountKey = it
            }
        }
        TODO("дописать логику")

        dao.getActiveAccount()?.let {
            firebaseAPI.saveAccount(accountName)
        }

    }

    /*        dao.getAccountsKey()
            .onEach {
                if (it.isEmpty()) {
                    runBlocking {
                        launch(Dispatchers.IO) {
                            dao.save(firebaseAPI.getAccounts())
                        }
                    }
                }
            }*/

    override fun setActiveAccount(accountName: String) = flow {
        val request = dao.save(accountName)
        if (request == 1) emit(Unit)
        else throw Exception()
    }
}