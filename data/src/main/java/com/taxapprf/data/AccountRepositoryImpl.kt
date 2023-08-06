package com.taxapprf.data

import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.domain.AccountRepository
import com.taxapprf.domain.user.AccountModel
import com.taxapprf.domain.user.SaveAccountModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val firebaseAccountDao: FirebaseAccountDaoImpl,
) : AccountRepository {
    override fun getAccounts() = flow {
        val accounts = firebaseAccountDao.getAccounts().toListAccountModel()
        if (accounts.isEmpty()) {
            firebaseAccountDao.saveDefaultAccount()
            emit(firebaseAccountDao.getAccounts().toListAccountModel())
        } else emit(accounts)
    }

    override fun saveAccount(saveAccountModel: SaveAccountModel) = flow {
        firebaseAccountDao.saveAccount(saveAccountModel.toFirebaseAccountModel())
        emit(Unit)
    }

    private fun List<FirebaseAccountModel>.toListAccountModel() = mapNotNull {
        with(it) {
            AccountModel(
                name = name ?: "",
                active = active ?: false
            )
        }
    }

    private fun SaveAccountModel.toFirebaseAccountModel() =
        FirebaseAccountModel(name.trim(), active)
}