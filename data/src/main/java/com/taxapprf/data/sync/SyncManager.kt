package com.taxapprf.data.sync

import com.taxapprf.domain.Sync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch

const val IS_SYNC = "is_sync"
const val IS_DELETE = "is_delete"
const val SYNC_AT = "sync_at"

abstract class SyncManager<L : Sync, A : Sync, R> {
    abstract fun observeLocal(): Flow<List<L>>
    abstract fun getLocal(): List<L>
    abstract fun L.mapLocalToApp(): A
    abstract fun List<A>.mapAppToLocal(): List<L>

    abstract fun observeRemote(): Flow<Result<List<A>>>
    abstract fun List<A>.mapAppToRemote(): Map<String, R>

    abstract fun saveLocal(models: List<L>)
    abstract fun deleteLocal(models: List<L>)
    abstract suspend fun saveRemote(models: Map<String, R>)
    abstract suspend fun deleteRemote(models: Map<String, R?>)

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

                        println("! 3 ! $cache")
                        cache.sync(
                            models,
                            saveLocal = { saveLocal(it.mapAppToLocal()) },
                            deleteLocal = { deleteLocal(it.mapAppToLocal()) },
                            saveRemote = { launch { saveRemote(it.mapAppToRemote()) } },
                            deleteRemote = { launch { deleteRemote(it) } }
                        )
                    }
                }
            }
        }

    fun <T : Sync> MutableMap<String, T>.sync(
        remoteList: List<T>,
        saveLocal: (List<T>) -> Unit,
        deleteLocal: (List<T>) -> Unit,
        saveRemote: (List<T>) -> Unit,
        deleteRemote: (Map<String, R?>) -> Unit,
    ) {
        val cache = this
        val saveLocalList = mutableListOf<T>()
        val deleteLocalList = mutableListOf<T>()

        val saveRemoteList = mutableListOf<T>()
        val deleteRemoteList = mutableMapOf<String, R?>()

        println("@@@@@!! 1")
        remoteList.map {
            println("@@@@@!! 2")
            if (cache.isCached(it.key)) {
                println("@@@@@!! 3")
                if (cache.isExpired(it)) {
                    println("@@@@@!! 6")
                    saveLocalList.add(it)
                } else if (cache.isNotSync(it.key)) {
                    println("@@@@@!! 4")
                    if (cache.isDelete(it.key)) {
                        println("@@@@@!! 5")
                        deleteRemoteList[it.key] = null
                    } else saveRemoteList.add(it)
                }

                cache.remove(it.key)
            } else saveLocalList.add(it)
        }

        cache.map {
            if (it.value.isSync) deleteLocalList.add(it.value)
            else saveRemoteList.add(it.value)
        }

        if (saveLocalList.isNotEmpty()) saveLocal(saveLocalList)
        if (deleteLocalList.isNotEmpty()) deleteLocal(deleteLocalList)
        if (saveRemoteList.isNotEmpty()) saveRemote(saveRemoteList)
        if (deleteRemoteList.isNotEmpty()) deleteRemote(deleteRemoteList)
    }

    private fun <T : Sync> MutableMap<String, T>.isCached(key: String) =
        containsKey(key)

    private fun <T : Sync> MutableMap<String, T>.isNotSync(key: String) =
        !getValue(key).isSync

    private fun <T : Sync> MutableMap<String, T>.isExpired(sync: T) =
        getValue(sync.key).syncAt <= sync.syncAt

    private fun <T : Sync> MutableMap<String, T>.isDelete(key: String) =
        !getValue(key).isDelete
}