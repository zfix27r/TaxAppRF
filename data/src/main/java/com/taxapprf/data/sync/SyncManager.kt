package com.taxapprf.data.sync

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext


const val REMOTE_KEY = "remote_key"
const val SYNC_AT = "sync_at"

abstract class SyncManager<LocalIn : SyncLocal, LocalOut : SyncLocal, Remote : SyncRemote> {
    protected abstract fun getLocalList(): List<LocalIn>
    protected abstract fun saveLocalList(locals: List<LocalOut>): List<Long>
    protected abstract fun deleteLocalList(locals: List<LocalOut>): Int

    protected abstract suspend fun getRemoteList(): List<Remote>
    protected abstract suspend fun updateRemoteList(remoteMap: Map<String, Remote?>)

    protected abstract suspend fun LocalIn.updateRemoteKey(): LocalIn?
    protected abstract fun Remote.toLocal(local: LocalIn? = null): LocalOut?
    protected abstract fun LocalIn.toRemote(remote: Remote? = null): Remote
    protected abstract fun LocalIn.toLocalOut(): LocalOut

    protected suspend fun startSync() {
        val saveLocalList = mutableListOf<LocalOut>()
        val deleteLocalList = mutableListOf<LocalOut>()

        val updateRemoteList = mutableMapOf<String, Remote?>()

        val remotes = getRemoteList()
        val locals = getLocalList().toMapLocalList()

        for (remote in remotes) {
            val remoteKey = remote.key ?: continue
            val remoteSyncAt = remote.syncAt ?: 0

            if (!locals.containsKey(remoteKey)) {
                remote.toLocal()?.let { saveLocalList.add(it) }
                continue
            }

            locals.getValue(remoteKey).let { local ->
                local.remoteKey?.let { localRemoteKey ->
                    if (local.syncAt < remoteSyncAt)
                        remote.toLocal(local)?.let { saveLocalList.add(it) }
                    else if (local.syncAt > remoteSyncAt)
                        updateRemoteList[localRemoteKey] = local.toRemote(remote)
                    else {

                    }
                }

                locals.remove(remote.key)
            }
        }

        locals.map { entry ->
            val local = entry.value

            local.remoteKey?.let {
                deleteLocalList.add(local.toLocalOut())
            } ?: run {
                local.updateRemoteKey()?.let {
                    it.remoteKey?.let { localRemoteKey ->
                        saveLocalList.add(it.toLocalOut())
                        updateRemoteList[localRemoteKey] = it.toRemote()
                    }
                }
            }
        }

        println("saveLocalList $saveLocalList")
        println("deleteLocalList $deleteLocalList")
        println("updateRemoteList $updateRemoteList")

        if (saveLocalList.isNotEmpty()) saveLocalList(saveLocalList)
        if (deleteLocalList.isNotEmpty()) deleteLocalList(deleteLocalList)
        if (updateRemoteList.isNotEmpty())
            withContext(coroutineContext) { launch { updateRemoteList(updateRemoteList) } }
    }

    private fun List<LocalIn>.toMapLocalList(): MutableMap<String?, LocalIn> {
        val mapLocalList = mutableMapOf<String?, LocalIn>()
        map { mapLocalList.put(it.remoteKey, it) }
        return mapLocalList
    }
}
