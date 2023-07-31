package com.taxapprf.domain

import com.taxapprf.domain.user.AccountModel
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.UserModel
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun signUp(signUpModel: SignUpModel): Flow<Unit>
    fun signIn(signInModel: SignInModel): Flow<Unit>
    fun signOut(): Flow<Unit>
    fun getUser(): Flow<UserModel?>
    fun saveAccount(accountModel: AccountModel): Flow<Unit>
}