package com.taxapprf.data

import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.domain.AccountRepository
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.SaveAccountModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val firebaseAccountDao: FirebaseAccountDaoImpl,
) : AccountRepository {
    override fun getAccounts() = flow {
        val accounts = firebaseAccountDao.getAccounts()
        if (accounts.isEmpty()) {
            firebaseAccountDao.saveDefaultAccount()
            emit(firebaseAccountDao.getAccounts())
        } else emit(accounts)
    }

    override fun saveAccount(saveAccountModel: SaveAccountModel) = flow {
/*        if (saveAccountModel.active)
            firebaseAccountDao.firebaseAccountDao.saveAccount(saveAccountModel.toFirebaseAccountModel())*/
        emit(Unit)
    }

    override fun changeAccount(oldAccountModel: AccountModel, newAccountModel: AccountModel) =
        flow {
            firebaseAccountDao.saveAccounts(
                mapOf(
                    oldAccountModel.name to oldAccountModel.toFirebaseAccountModel(),
                    newAccountModel.name to newAccountModel.toFirebaseAccountModel()
                )
            )
            emit(Unit)
        }

    private fun AccountModel.toFirebaseAccountModel() =
        FirebaseAccountModel(name.trim(), !active)
}