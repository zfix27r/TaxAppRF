package com.taxapprf.data.sync

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

const val IS_SYNC = "is_sync"
const val IS_DELETE = "is_delete"
const val SYNC_AT = "sync_at"

abstract class SyncManager<Local : SyncLocal, Remote : SyncRemote> {
    protected abstract fun getLocalList(): List<Local>
    protected abstract fun saveLocalList(locals: List<Local>): List<Long>
    protected abstract fun deleteLocalList(locals: List<Local>): Int

    protected abstract suspend fun getRemoteList(): List<Remote>
    protected abstract suspend fun updateRemoteList(remoteMap: Map<String, Remote?>)

    protected abstract suspend fun Local.updateRemoteKey(): Local?
    protected abstract fun Remote.toLocal(local: Local? = null): Local?
    protected abstract fun Local.toRemote(remote: Remote? = null): Remote

    suspend fun sync() {
        val saveLocalList = mutableListOf<Local>()
        val deleteLocalList = mutableListOf<Local>()

        val updateRemoteList = mutableMapOf<String, Remote?>()

        val remotes = getRemoteList()
        val locals = getLocalList().toMapLocalList()

        for (remote in remotes) {
            val remoteKey = remote.key ?: continue

            if (!locals.containsKey(remoteKey)) {
                remote.toLocal()?.let { saveLocalList.add(it) }
                continue
            }

            locals.getValue(remoteKey).let { local ->
                val remoteSyncAt = remote.syncAt ?: 0

                if (local.syncAt < remoteSyncAt) {
                    remote.toLocal(local)?.let { saveLocalList.add(it) }
                } else if (!local.isSync) {
                    if (local.isDelete)
                        updateRemoteList[local.key] = null
                    else
                        updateRemoteList[local.key] = local.toRemote(remote)
                } else if (local.syncAt > remoteSyncAt)
                    updateRemoteList[local.key] = local.toRemote(remote)

                locals.remove(remote.key)
            }
        }

        locals.map { entry ->
            val local = entry.value

            if (local.isSync || local.isDelete)
                deleteLocalList.add(local)
            else {
                if (local.key == "") {
                    local.updateRemoteKey()?.let {
                        saveLocalList.add(it)
                        updateRemoteList[local.key] = it.toRemote()
                    }
                } else
                    updateRemoteList[local.key] = local.toRemote()
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

    private fun List<Local>.toMapLocalList(): MutableMap<String, Local> {
        val mapLocalList = mutableMapOf<String, Local>()
        map { mapLocalList.put(it.key, it) }
        return mapLocalList
    }
}