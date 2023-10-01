package com.taxapprf.domain

import com.taxapprf.domain.user.ObserveUserWithAccountsModel
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.SwitchAccountModel
import com.taxapprf.domain.user.UserModel
import com.taxapprf.domain.user.UserWithAccountsModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUserWithAccounts(observeUserWithAccountsModel: ObserveUserWithAccountsModel): Flow<UserWithAccountsModel>
    suspend fun signUp(signUpModel: SignUpModel)
    suspend fun signIn(signInModel: SignInModel)
    suspend fun signOut()
    suspend fun saveUser(userModel: UserModel)
    suspend fun switchAccount(switchAccountModel: SwitchAccountModel)
    suspend fun deleteAll()
}