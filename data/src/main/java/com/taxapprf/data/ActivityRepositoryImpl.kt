package com.taxapprf.data

import com.taxapprf.data.error.AuthErrorUndefined
import com.taxapprf.data.local.dao.AccountDao
import com.taxapprf.data.local.dao.UserDao
import com.taxapprf.data.local.entity.AccountEntity
import com.taxapprf.data.local.entity.UserEntity
import com.taxapprf.domain.ActivityRepository
import com.taxapprf.domain.account.AccountModel
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
    override fun isSignIn() = firebaseAPI.isSignIn()
    override fun getUser(): Flow<UserModel> {
        TODO("Not yet implemented")
    }

    override fun signIn(signInModel: SignInModel) = flow {
        firebaseAPI.signIn(signInModel)
        /*        accountDao.getActiveAccount()
                    ?.let { firebaseAPI.accountKey = it }
                    ?: run { userDao.save(firebaseAPI.getUserEntity()) }*/
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

    override fun getAccounts() = accountDao.getAccounts().map { l ->
        l.map {
            if (it.active) firebaseAPI.accountKey = it.name
            it.toAccountModel()
        }
    }

    override fun saveAccount(accountModel: AccountModel) = flow {
        accountDao.save(accountModel.toAccountEntity())
        emit(Unit)
    }

    private fun AccountEntity.toAccountModel() = AccountModel(name, active)
    private fun AccountModel.toAccountEntity() = AccountEntity(0, name, active)
    private fun SignUpModel.toUserEntity() = UserEntity(0, true, name, email, phone)
}