package com.taxapprf.data

import com.taxapprf.data.remote.firebase.FirebaseUserDaoImpl
import com.taxapprf.domain.UserRepository
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.UserModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firebaseUserDao: FirebaseUserDaoImpl,
) : UserRepository {
    override fun observeUser() = flow {
        emit(firebaseUserDao.getProfile())
    }

    override suspend fun saveUser(userModel: UserModel) =
        firebaseUserDao.saveProfile(userModel)

    override suspend fun signIn(signInModel: SignInModel) =
        firebaseUserDao.signInWithEmailAndPassword(signInModel)

    override suspend fun signUp(signUpModel: SignUpModel) =
        firebaseUserDao.signUpWithEmailAndPassword(signUpModel)

    override suspend fun signOut() {
        firebaseUserDao.signOut()
    }

    override suspend fun isSignIn() =
        firebaseUserDao.isSignIn()
}