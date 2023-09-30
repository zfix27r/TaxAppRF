package com.taxapprf.data.remote.firebase.dao

import com.google.firebase.auth.FirebaseUser
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.UserModel


interface RemoteUserDao {
    suspend fun signInWithEmailAndPassword(signInModel: SignInModel)
    suspend fun signUpWithEmailAndPassword(signUpModel: SignUpModel)
    fun signOut()
    fun getUser(): FirebaseUser?
    suspend fun updateUser(userModel: UserModel)
}