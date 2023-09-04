package com.taxapprf.data.sync

import com.taxapprf.data.sync
import com.taxapprf.domain.Sync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class SyncManager<L : Sync, A : Sync, R>() {
    abstract fun observeLocal(): Flow<List<L>>
    abstract fun getLocal(): List<L>
    abstract fun L.mapLocalToApp(): A
    abstract fun List<A>.mapAppToLocal(): List<L>

    abstract fun observeRemote(): Flow<Result<List<A>>>
    abstract fun List<A>.mapAppToRemote(): Map<String, R>

    abstract fun saveLocal(models: List<L>)
    abstract fun deleteLocal(models: List<L>)
    abstract suspend fun saveRemote(models: Map<String, R>)

    fun observe() =
        channelFlow {
            launch {
                observeLocal().collectLatest { models ->
                    if (models.isNotEmpty()) send(models.map { it.mapLocalToApp() })
                    else send(emptyList<A>())
                }
            }

            launch {
                observeRemote().collectLatest { result ->
                    result.getOrNull()?.let { models ->
                        val cache = mutableMapOf<String, A>()

                        getLocal().map {
                            val model = it.mapLocalToApp()
                            cache[model.key] = model
                        }

                        cache.sync(
                            models,
                            saveLocal = { saveLocal(it.mapAppToLocal()) },
                            deleteLocal = { deleteLocal(it.mapAppToLocal()) },
                            saveRemote = { launch { saveRemote(it.mapAppToRemote()) } }
                        )
                    }
                }
            }
        }
}