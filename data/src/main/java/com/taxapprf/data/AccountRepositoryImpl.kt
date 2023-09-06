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
        SyncAccounts(localAccountDao, remoteAccountDao).observe()

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
                key = passiveAccountKey,
                isActive = false,
                isSync = true,
                isDeferredDelete = false,
                syncAt = syncAt
            ),
            LocalAccountEntity(
                key = activeAccountKey,
                isActive = true,
                isSync = true,
                isDeferredDelete = false,
                syncAt = syncAt
            ),
        )

    private fun SwitchAccountModel.toMapFirebaseAccountModel(syncAt: Long) =
        mapOf(
            passiveAccountKey to FirebaseAccountModel(passiveAccountKey, false, syncAt),
            activeAccountKey to FirebaseAccountModel(activeAccountKey, true, syncAt)
        )
}