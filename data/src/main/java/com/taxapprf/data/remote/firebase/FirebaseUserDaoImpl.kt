package com.taxapprf.data.remote.firebase

import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.taxapprf.data.error.DataErrorUser
import com.taxapprf.data.remote.firebase.dao.RemoteUserDao
import com.taxapprf.data.safeCall
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.UserModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseUserDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : RemoteUserDao {
    override suspend fun signInWithEmailAndPassword(signInModel: SignInModel) {
        safeCall {
            with(signInModel) {
                fb.auth.signInWithEmailAndPassword(email, password).await()
            }
        }
    }

    override suspend fun signUpWithEmailAndPassword(signUpModel: SignUpModel) {
        safeCall {
            with(signUpModel) {
                fb.auth.createUserWithEmailAndPassword(email, password).await()
            }
        }
    }

    override fun signOut() {
        safeCall {
            fb.auth.signOut()
        }
    }

    override fun getUser() =
        safeCall {
            fb.auth.currentUser
        }

    override suspend fun updateUser(userModel: UserModel) {
        safeCall {
            val profile = userProfileChangeRequest {
                displayName = userModel.name
            }

            fb.auth.currentUser?.updateProfile(profile) ?: throw DataErrorUser()
        }
    }
}