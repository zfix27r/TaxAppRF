package com.taxapprf.data

import com.taxapprf.domain.NetworkManager
import com.taxapprf.domain.SyncRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class SyncRepositoryImpl(
    private val networkManager: NetworkManager,
) : SyncRepository {
    override fun syncAll() {
        if (networkManager.available) {
            observeAllRemote().collectLatest { result ->
                result.getOrNull()?.let { remotes ->
                    println("remotes $remotes")
//                            locals.sync(remotes)
                }
            }

        }
    }


    private suspend fun MutableMap<String, Local>.sync(remotes: List<Remote>) {
        val locals = this

        remotes.map { remote ->
            val remoteKey = remote.key ?: "-"

            if (locals.containsKey(remoteKey)) {
                locals.getValue(remoteKey).let { local ->
                    val remoteSyncAt = remote.syncAt ?: 0

                    if (local.syncAt < remoteSyncAt) {
                        remote.toLocal(local)?.let { saveLocalList.add(it) }
                    } else if (!local.isSync) {
                        if (local.isDelete) deleteLocalAndRemote(local, remote)
                        else saveRemoteList.add(local)
                    } else if (local.syncAt > remoteSyncAt)
                        saveRemoteList.add(local)

                    locals.remove(remote.key)
                }
            } else remote.toLocal()?.let { saveLocalList.add(it) }
        }

        locals.map {
            val local = it.value
            if (local.isSync || local.isDelete) deleteLocalList.add(local)
            else saveRemoteList.add(local)
        }

        println("saveLocalList $saveLocalList")
        println("deleteLocalList $deleteLocalList")
        println("saveRemoteList $saveRemoteList")
        println("deleteRemoteList $deleteRemoteList")

        if (saveLocalList.isNotEmpty()) saveAllLocal(saveLocalList)
        if (deleteLocalList.isNotEmpty()) deleteAllLocal(deleteLocalList)
        /*        if (saveRemoteList.isNotEmpty())
                    withContext(coroutineContext) { launch { saveAllRemote(saveRemoteList) } }*/
        if (deleteRemoteList.isNotEmpty())
            withContext(coroutineContext) { launch { deleteAllRemote(deleteRemoteList) } }
    }
}