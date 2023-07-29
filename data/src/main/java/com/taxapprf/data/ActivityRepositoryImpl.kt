package com.taxapprf.data

import com.taxapprf.data.local.dao.AccountDao
import com.taxapprf.data.local.model.FirebaseAccountModel
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
    private val dao: AccountDao
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

    override fun getAccounts() = flow<List<String>> {
        firebaseAPI.getAccounts()
    }

    /*        dao.getAccountsKey()
            .onEach {
                if (it.isEmpty()) {
                    runBlocking {
                        launch(Dispatchers.IO) {
                            dao.save(firebaseAPI.getAccounts())
                        }
                    }
                }
            }*/

    override fun setActiveAccount(accountName: String) = flow {
        val request = dao.save(accountName)
        if (request == 1) emit(Unit)
        else throw Exception()
    }
}