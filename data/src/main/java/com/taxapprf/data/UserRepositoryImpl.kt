package com.taxapprf.data

import android.net.Uri
import com.taxapprf.data.local.room.LocalAccountDao
import com.taxapprf.data.local.room.LocalUserDao
import com.taxapprf.data.local.room.model.LocalUserWithAccounts
import com.taxapprf.data.remote.firebase.dao.RemoteUserDao
import com.taxapprf.domain.UserRepository
import com.taxapprf.domain.user.AccountModel
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.UserModel
import com.taxapprf.domain.user.UserWithAccountsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userLocalDao: LocalUserDao,
    private val userRemoteDao: RemoteUserDao,
    private val accountLocalDao: LocalAccountDao,
) : UserRepository {
    override fun observeUserWithAccounts(): Flow<UserWithAccountsModel> {
        val email = userRemoteDao.getUser()?.email ?: LOCAL_USER_EMAIL

        return userLocalDao.observe(email).map { userWithAccounts ->
            UserWithAccountsModel(
                user = userWithAccounts.first().toUserModel(),
                activeAccount = userWithAccounts.find { it.isAccountActive }?.toAccountModel(),
                otherAccounts = userWithAccounts.filter { !it.isAccountActive }.map { it.toAccountModel() }
            )
        }
    }

    override suspend fun saveUser(userModel: UserModel) =
        userRemoteDao.updateUser(userModel)

    override suspend fun signIn(signInModel: SignInModel) =
        userRemoteDao.signInWithEmailAndPassword(signInModel)

    override suspend fun signUp(signUpModel: SignUpModel) =
        userRemoteDao.signUpWithEmailAndPassword(signUpModel)

    override suspend fun signOut() {
        userRemoteDao.signOut()
    }

    override suspend fun switchAccount(accountId: Int) {
        accountLocalDao.resetActiveAccount()
        accountLocalDao.setActiveAccount(accountId)
    }

    private fun LocalUserWithAccounts.toUserModel() =
        UserModel(email, avatar?.let { Uri.parse(it) }, name, phone)

    private fun LocalUserWithAccounts.toAccountModel() =
        AccountModel(accountId, accountName)

    companion object {
        const val LOCAL_USER_EMAIL = "local"
    }
}