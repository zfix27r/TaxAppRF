package com.taxapprf.data

import com.taxapprf.data.local.room.dao.LocalAccountDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.domain.AccountRepository
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.SwitchAccountModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val localAccountDao: LocalAccountDao,
    private val firebaseAccountDao: FirebaseAccountDaoImpl,
) : AccountRepository {
    override fun getAccounts() = channelFlow {
        launch {
            localAccountDao.observeAll().collectLatest {
                send(it.toListAccountModel())
            }
        }

        launch {
            firebaseAccountDao.observeAccounts().collectLatest { result ->
                result.getOrNull()?.let {
                    localAccountDao.deleteAll()
                    localAccountDao.save(it.toListLocalAccountEntity())
                }
            }
        }
    }

    override fun switchAccount(switchAccountModel: SwitchAccountModel): Flow<Unit> =
        flow {
            with(switchAccountModel) {
                firebaseAccountDao.saveAccounts(
                    mapOf(
                        oldAccountName to FirebaseAccountModel(oldAccountName, false),
                        newAccountName to FirebaseAccountModel(newAccountName, true)
                    )
                )
            }
            emit(Unit)
        }

    private fun List<LocalAccountEntity>.toListAccountModel() = map {
        AccountModel(it.accountKey, it.isActive)
    }

    private fun List<AccountModel>.toListLocalAccountEntity() = map {
        LocalAccountEntity(it.accountKey, it.isActive)
    }
}