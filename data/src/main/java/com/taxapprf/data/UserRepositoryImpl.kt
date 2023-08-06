package com.taxapprf.data

import com.taxapprf.data.remote.firebase.FirebaseUserDaoImpl
import com.taxapprf.domain.UserRepository
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseUserDao: FirebaseUserDaoImpl
) : UserRepository {
    override fun getUser() = flow {
        emit(firebaseUserDao.getProfile())
    }

    override fun signIn(signInModel: SignInModel) = flow {
        emit(firebaseUserDao.signInWithEmailAndPassword(signInModel))
    }

    override fun signUp(signUpModel: SignUpModel) = flow {
        emit(firebaseUserDao.signUpWithEmailAndPassword(signUpModel))
    }

    override fun signOut() = flow {
        emit(firebaseUserDao.signOut())
    }

    override fun isSignIn() = firebaseUserDao.isSignIn()
}