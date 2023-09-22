package com.taxapprf.data

import com.taxapprf.data.local.room.dao.LocalAccountDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.model.LocalAccountSwitchModel
import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.sync.SyncAccounts
import com.taxapprf.domain.AccountRepository
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.AccountsModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val localDao: LocalAccountDao,
    private val remoteDao: FirebaseAccountDaoImpl,
) : AccountRepository {
    override fun observeAccounts() =
        localDao.observeAll()
            .map { accounts ->
                AccountsModel(
                    active = accounts.find { it.isActive }?.toAccountModel(),
                    inactive = accounts.filter { !it.isActive }.map { it.toAccountModel() }
                )
            }

    override suspend fun syncAccounts() =
        SyncAccounts(localDao, remoteDao).sync()

    override suspend fun switchAccount(accountId: Int) {
        localDao.resetActiveAccount()
        localDao.setActiveAccount(accountId)
    }

    private fun LocalAccountEntity.toAccountModel() =
        AccountModel(id, key, isActive)
}