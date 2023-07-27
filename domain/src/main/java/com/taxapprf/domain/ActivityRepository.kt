package com.taxapprf.domain

import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.SignInModel
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun signUp(signUpModel: SignUpModel): Flow<Unit>
    fun signIn(signInModel: SignInModel): Flow<Unit>
    fun signOut(): Flow<Unit>
    fun isSignIn(): Boolean
    fun getAccounts(): Flow<List<String>>
    fun setActiveAccount(accountName: String): Flow<Unit>
}