package com.taxapprf.data

import com.taxapprf.data.local.room.dao.LocalAccountDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.data.sync.SyncAccounts
import com.taxapprf.domain.AccountRepository
import com.taxapprf.domain.account.SwitchAccountModel
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val localAccountDao: LocalAccountDao,
    private val remoteAccountDao: FirebaseAccountDaoImpl,
) : AccountRepository {
    override fun getAccounts() =
        SyncAccounts(localAccountDao, remoteAccountDao).observeAll()

    override fun switchAccount(switchAccountModel: SwitchAccountModel) =
        flow {
            with(switchAccountModel) {
                val syncAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                localAccountDao.save(toListLocalAccountEntity(syncAt))
                remoteAccountDao.saveAll(toMapFirebaseAccountModel(syncAt))
            }

            emit(Unit)
        }

    private fun SwitchAccountModel.toListLocalAccountEntity(syncAt: Long) =
        listOf(
            LocalAccountEntity(
                id = inactiveAccountId,
                key = inactiveAccountKey,
                isActive = false,
                isSync = true,
                isDelete = false,
                syncAt = syncAt
            ),
            LocalAccountEntity(
                id = activeAccountId ?: 0,
                key = activeAccountKey,
                isActive = true,
                isSync = true,
                isDelete = false,
                syncAt = syncAt
            ),
        )

    private fun SwitchAccountModel.toMapFirebaseAccountModel(syncAt: Long) =
        mapOf(
            inactiveAccountKey to FirebaseAccountModel(inactiveAccountKey, false, syncAt),
            activeAccountKey to FirebaseAccountModel(activeAccountKey, true, syncAt)
        )
}