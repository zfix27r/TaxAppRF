package com.taxapprf.data

import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.domain.AccountRepository
import com.taxapprf.domain.account.AccountModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEmpty
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val firebaseAccountDao: FirebaseAccountDaoImpl,
) : AccountRepository {
    override fun getAccounts() = firebaseAccountDao.getAccounts()
        .onEmpty { firebaseAccountDao.saveDefaultAccount() }

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