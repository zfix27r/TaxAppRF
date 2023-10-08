package com.taxapprf.data.sync


const val REMOTE_KEY = "remote_key"
const val IS_SYNC = "is_sync"
const val SYNC_AT = "sync_at"

abstract class SyncManager<LocalIn : SyncLocal, LocalOut : SyncLocal, Remote : SyncRemote> {
    protected abstract fun getLocalInList(): List<LocalIn>
    protected abstract fun saveLocalOutList(locals: List<LocalOut>): List<Long>
    protected abstract fun deleteLocalOutList(locals: List<LocalOut>): Int

    protected abstract suspend fun getRemoteList(): List<Remote>
    protected abstract suspend fun updateRemoteList(remoteMap: Map<String, Remote?>)

    protected abstract suspend fun LocalIn.updateRemoteKey(): LocalIn?
    protected abstract fun Remote.toLocalOut(localIn: LocalIn? = null): LocalOut?
    protected abstract fun LocalIn.toRemote(): Remote
    protected abstract fun LocalIn.toLocalOut(): LocalOut

    protected suspend fun startSync() {
        val saveLocalList = mutableListOf<LocalOut>()
        val deleteLocalList = mutableListOf<LocalOut>()

        val updateRemoteMap = mutableMapOf<String, Remote?>()

        val remoteList = getRemoteList()
        println("remotes $remoteList")
        val localMap = mutableMapOf<String, LocalIn>()

        getLocalInList().map { localIn ->
            if (localIn.remoteKey == null) {
                localIn.updateRemoteKey()?.let {
                    it.remoteKey?.let { localRemoteKey ->
                        saveLocalList.add(it.toLocalOut())
                        updateRemoteMap[localRemoteKey] = it.toRemote()
                    }
                }
            } else
                localMap[localIn.remoteKey!!] = localIn
        }
        println("locals $localMap")

        for (remote in remoteList) {
            val remoteKey = remote.key ?: continue
            val remoteSyncAt = remote.syncAt ?: 0

            if (localMap.containsKey(remoteKey)) {
                localMap.getValue(remoteKey).let { localIn ->
                    if (localIn.syncAt <= remoteSyncAt)
                        remote.toLocalOut(localIn)?.let { saveLocalList.add(it) }
                    else if (localIn.syncAt > remoteSyncAt)
                        updateRemoteMap[remoteKey] = localIn.toRemote()

                    localMap.remove(remote.key)
                }
            } else
                remote.toLocalOut()?.let { saveLocalList.add(it) }
        }

        localMap.map { entry ->
            entry.value.let { localIn ->
                if (localIn.isSync)
                    deleteLocalList.add(localIn.toLocalOut())
                else {
                    localIn.remoteKey?.let {
                        updateRemoteMap[it] = localIn.toRemote()
                    }
                }
            }
        }

        println("< syncManager")
        println("saveLocalList $saveLocalList")
        println("deleteLocalList $deleteLocalList")
        println("updateRemoteList $updateRemoteMap")
        println("syncManager >")

        if (saveLocalList.isNotEmpty()) saveLocalOutList(saveLocalList)
        if (deleteLocalList.isNotEmpty()) deleteLocalOutList(deleteLocalList)
        if (updateRemoteMap.isNotEmpty()) updateRemoteList(updateRemoteMap)
    }
}