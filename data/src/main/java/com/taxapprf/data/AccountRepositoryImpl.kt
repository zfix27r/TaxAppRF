package com.taxapprf.data

import com.taxapprf.data.local.room.dao.LocalAccountDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.sync.SyncAccounts
import com.taxapprf.domain.AccountRepository
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.SwitchAccountModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val localDao: LocalAccountDao,
    private val remoteDao: FirebaseAccountDaoImpl,
) : AccountRepository {
    override fun getAccounts() =
        SyncAccounts(localDao, remoteDao).observeAll()

    override fun observeAccounts() {
        remoteDao.observer({})
    }

    override suspend fun switchAccount(switchAccountModel: SwitchAccountModel) {
        localDao.saveAll(switchAccountModel.toListLocalAccountEntity())
    }

    private fun SwitchAccountModel.toListLocalAccountEntity() =
        listOf(
            LocalAccountEntity(
                id = inactiveAccountId,
                key = inactiveAccountKey,
                isActive = false,
                isSync = false,
                isDelete = false,
                syncAt = getTime()
            ),
            LocalAccountEntity(
                id = activeAccountId ?: 0,
                key = activeAccountKey,
                isActive = true,
                isSync = false,
                isDelete = false,
                syncAt = getTime()
            )
        )
}