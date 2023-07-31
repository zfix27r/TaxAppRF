package com.taxapprf.data

import com.taxapprf.data.error.AuthErrorUndefined
import com.taxapprf.data.local.dao.AccountDao
import com.taxapprf.data.local.dao.UserDao
import com.taxapprf.data.local.entity.AccountEntity
import com.taxapprf.data.local.entity.UserEntity
import com.taxapprf.data.local.model.UserWithAccountModel
import com.taxapprf.domain.ActivityRepository
import com.taxapprf.domain.user.AccountModel
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val firebaseAPI: FirebaseAPI,
    private val userDao: UserDao,
    private val accountDao: AccountDao
) : ActivityRepository {
    override fun getUser() = userDao.getSignIn().map { models ->
        if (models.isNotEmpty()) {
            val accounts = models.mapNotNull {
                it.accountActive?.let { _ ->
                    if (it.accountActive) firebaseAPI.accountKey = it.accountName!!
                    it.toAccountModel()
                }
            }

            UserModel(
                name = models.first().name,
                email = models.first().email,
                phone = models.first().phone,
                accounts = accounts
            )
        } else null
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
        emit(firebaseAPI.signOut())
    }

    override fun saveAccount(accountModel: AccountModel): Flow<Unit> = flow {
        val user = userDao.getNameActiveUser()
        accountDao.save(accountModel.toAccountEntity(user))
        emit(Unit)
    }

    private fun UserWithAccountModel.toAccountModel() = AccountModel(accountName!!, accountActive!!)
    private fun AccountModel.toAccountEntity(user: String) =
        AccountEntity(name = name, user = user, active = active)

    private fun SignUpModel.toUserEntity() = UserEntity(name, true, email, phone)
}