package com.taxapprf.data

import com.taxapprf.data.local.dao.ActivityDao
import com.taxapprf.domain.ActivityRepository
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignUpModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val firebaseAPI: FirebaseAPI,
    private val dao: ActivityDao
) : ActivityRepository {
    override fun isSignIn() = firebaseAPI.isSignIn()

    override fun signIn(signInModel: SignInModel) = flow {
        emit(firebaseAPI.signIn(signInModel))
    }

    override fun signUp(signUpModel: SignUpModel) = flow {
        emit(firebaseAPI.signUp(signUpModel))
    }

    override fun signOut() = flow {
        emit(firebaseAPI.signOut())
    }

    override fun getAccounts() = dao.getAccounts()
        .onEach {
            if (it.isEmpty()) {
                runBlocking {
                    launch(Dispatchers.IO) {
                        dao.save(firebaseAPI.getAccounts())
                    }
                }
            }
        }

    override fun setActiveAccount(accountName: String) = flow {
        val request = dao.save(accountName)
        if (request == 1) emit(Unit)
        else throw Exception()
    }
}