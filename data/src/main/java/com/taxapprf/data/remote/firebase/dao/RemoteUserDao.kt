package com.taxapprf.data.remote.firebase.dao

import com.google.firebase.auth.FirebaseUser
import com.taxapprf.domain.main.user.SignInModel
import com.taxapprf.domain.main.user.SignUpModel


interface RemoteUserDao {
    suspend fun signInWithEmailAndPassword(signInModel: SignInModel)
    suspend fun signUpWithEmailAndPassword(signUpModel: SignUpModel)
    fun signOut()
    fun getUser(): FirebaseUser?
    suspend fun updateUser(name: String)
}