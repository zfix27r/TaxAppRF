package com.taxapprf.data

import com.taxapprf.data.local.room.dao.LocalAccountDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.domain.AccountRepository
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.SwitchAccountModel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val localAccountDao: LocalAccountDao,
    private val firebaseAccountDao: FirebaseAccountDaoImpl,
) : AccountRepository {
    override fun getAccounts() = channelFlow {
        val local = mutableMapOf<String, AccountModel>()

        launch {
            localAccountDao.observeAll().collectLatest { accounts ->
                local.clear()
                send(
                    accounts.map {
                        val account = it.toAccountModel()
                        local[account.key] = account
                        account
                    }
                )
            }
        }

        launch {
            firebaseAccountDao.observeAccounts().collectLatest { result ->
                result.getOrNull()?.let { accounts ->
                    local.sync(
                        accounts,
                        saveLocal = {
                            localAccountDao.save(it.toListLocalAccountEntity())
                        },
                        deleteLocal = {
                            localAccountDao.delete(it.toListLocalAccountEntity())
                        },
                        saveRemote = {
                            launch {
                                firebaseAccountDao.saveAccounts(it.toMapFirebaseAccountModel())
                            }
                        }
                    )
                }
            }
        }
    }

    override fun switchAccount(switchAccountModel: SwitchAccountModel) =
        flow {
            with(switchAccountModel) {
                val syncAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                localAccountDao.save(toListLocalAccountEntity(syncAt))
                firebaseAccountDao.saveAccounts(toMapFirebaseAccountModel(syncAt))
            }

            emit(Unit)
        }

    private fun LocalAccountEntity.toAccountModel() =
        AccountModel(key, isActive, isSync, syncAt)

    private fun List<AccountModel>.toListLocalAccountEntity() = map {
        LocalAccountEntity(it.key, it.isActive, true, it.syncAt)
    }

    private fun List<AccountModel>.toMapFirebaseAccountModel(): MutableMap<String, FirebaseAccountModel> {
        val accounts = mutableMapOf<String, FirebaseAccountModel>()
        map {
            accounts.put(it.key, FirebaseAccountModel(it.key, it.isActive, it.syncAt))
        }
        return accounts
    }

    private fun SwitchAccountModel.toListLocalAccountEntity(syncAt: Long) =
        listOf(
            LocalAccountEntity(
                key = passiveAccountKey,
                isActive = false,
                isSync = true,
                syncAt = syncAt
            ),
            LocalAccountEntity(
                key = activeAccountKey,
                isActive = true,
                isSync = true,
                syncAt = syncAt
            ),
        )

    private fun SwitchAccountModel.toMapFirebaseAccountModel(syncAt: Long) =
        mapOf(
            passiveAccountKey to FirebaseAccountModel(passiveAccountKey, false, syncAt),
            activeAccountKey to FirebaseAccountModel(activeAccountKey, true, syncAt)
        )
}