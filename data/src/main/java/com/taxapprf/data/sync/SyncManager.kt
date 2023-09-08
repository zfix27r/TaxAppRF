package com.taxapprf.data.sync

import com.taxapprf.domain.Sync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val IS_SYNC = "is_sync"
const val IS_DELETE = "is_delete"
const val SYNC_AT = "sync_at"

abstract class SyncManager<L : Sync, A : Sync> {
    abstract fun observeLocal(): Flow<List<L>>
    abstract fun getLocal(): List<L>
    abstract fun L.mapLocalToApp(): A
    abstract fun A.mapAppToLocal(local: A? = null): L

    abstract fun observeRemote(): Flow<Result<List<A>>>
    abstract fun saveLocal(models: List<L>)
    abstract fun deleteLocal(models: List<L>)
    abstract suspend fun saveRemote(models: List<A>)
    abstract suspend fun deleteRemote(models: List<A>)

    fun observe() = observeAll().map {
        if (it.isEmpty()) it.first() else null
    }

    fun observeAll() =
        channelFlow {
            launch {
                observeLocal().collectLatest { models ->
                    println("! 1 ! $models")
                    if (models.isNotEmpty()) send(models.map { it.mapLocalToApp() })
                    else send(emptyList<A>())
                }
            }

            launch {
                observeRemote().collectLatest { result ->
                    result.getOrNull()?.let { models ->
                        println("! 2 ! $models")
                        val cache = mutableMapOf<String, A>()
                        getLocal().map {
                            val model = it.mapLocalToApp()
                            cache[model.key] = model
                        }

                        cache.sync(
                            models,
                            saveLocal = { saveLocal(it) },
                            deleteLocal = { deleteLocal(it) },
                            saveRemote = { launch { saveRemote(it) } },
                            deleteRemote = { launch { deleteRemote(it) } }
                        )
                    }
                }
            }
        }

    fun MutableMap<String, A>.sync(
        remoteList: List<A>,
        saveLocal: (List<L>) -> Unit,
        deleteLocal: (List<L>) -> Unit,
        saveRemote: (List<A>) -> Unit,
        deleteRemote: (List<A>) -> Unit,
    ) {
        val cache = this
        val saveLocalList = mutableListOf<L>()
        val deleteLocalList = mutableListOf<L>()

        val saveRemoteList = mutableListOf<A>()
        val deleteRemoteList = mutableListOf<A>()

        remoteList.map { remote ->
            if (cache.containsKey(remote.key)) {
                cache.getValue(remote.key).let { local ->
                    if (!local.isSync) {
                        if (local.isDelete) {
                            deleteRemoteList.add(local)
                            deleteLocalList.add(local.mapAppToLocal())
                        } else saveRemoteList.add(local)
                    } else if (local.syncAt < remote.syncAt) {
                        saveLocalList.add(remote.mapAppToLocal(local))
                    }

                    cache.remove(remote.key)
                }
            } else saveLocalList.add(remote.mapAppToLocal())
        }

        /*        cache.map {
                    if (it.value.isSync) deleteLocalList.add(it.value.mapAppToLocal())
                    else saveRemoteList.add(it.value)
                }*/

        println("saveLocalList $saveLocalList")
        println("deleteLocalList $deleteLocalList")
        println("saveRemoteList $saveRemoteList")
        println("deleteRemoteList $deleteRemoteList")

        if (saveLocalList.isNotEmpty()) saveLocal(saveLocalList)
        if (deleteLocalList.isNotEmpty()) deleteLocal(deleteLocalList)
        if (saveRemoteList.isNotEmpty()) saveRemote(saveRemoteList)
        if (deleteRemoteList.isNotEmpty()) deleteRemote(deleteRemoteList)
    }
}