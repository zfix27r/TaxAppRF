package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.UserModel


interface RemoteUserDao {
    suspend fun signInAnonymously()

    suspend fun signInWithEmailAndPassword(signInModel: SignInModel)

    suspend fun signUpWithEmailAndPassword(signUpModel: SignUpModel)

    fun signOut()

    fun isSignIn(): Boolean

    suspend fun getProfile(): UserModel?

    suspend fun saveProfile(userModel: UserModel)
}