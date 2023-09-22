package com.taxapprf.domain

import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(): Flow<UserModel?>
    suspend fun isSignIn(): Boolean
    suspend fun signUp(signUpModel: SignUpModel)
    suspend fun signIn(signInModel: SignInModel)
    suspend fun signOut()
    suspend fun saveUser(userModel: UserModel)
}