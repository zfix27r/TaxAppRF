package com.taxapprf.data.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

const val IS_SYNC = "is_sync"
const val IS_DELETE = "is_delete"
const val SYNC_AT = "sync_at"

abstract class SyncManager<Local : SyncLocal, Remote : SyncRemote, App> {
    protected abstract fun observeLocal(): Flow<Local?>
    protected abstract fun observeAllLocal(): Flow<List<Local>>

    protected abstract fun getLocal(): Local?
    protected abstract fun getAllLocal(): List<Local>

    protected abstract fun Local.toRemote(remote: Remote? = null): Remote
    protected abstract fun Local.toApp(): App

    protected abstract fun saveAllLocal(locals: List<Local>)
    protected abstract fun deleteAllLocal(locals: List<Local>)

    protected abstract fun observeRemote(): Flow<Result<Remote?>>
    protected abstract fun observeAllRemote(): Flow<Result<List<Remote>>>

    protected abstract fun Remote.toLocal(local: Local? = null): Local?

    protected abstract suspend fun saveAllRemote(locales: List<Local>)
    protected abstract suspend fun deleteAllRemote(remotes: List<Remote>)

    fun observe() =
        channelFlow {
            launch {
                observeLocal().collectLatest { local ->
                    println("local $local")
                    send(local?.toApp())
                }
            }

            launch {
                observeRemote().collectLatest { result ->
                    result.getOrNull()?.let { remote ->
                        println("remote $remote")
                        val locals = mutableMapOf<String, Local>()
                        val remotes = listOf(remote)
                        getLocal()?.let { locals[it.key] = it }
                        locals.sync(remotes)
                    }
                }

            }
        }

    fun observeAll() =
        channelFlow {
            launch {
                observeAllLocal().collectLatest { locals ->
                    println("locals $locals")
                    send(locals.map { it.toApp() })
                }
            }

            launch {
                observeAllRemote().collectLatest { result ->
                    result.getOrNull()?.let { remotes ->
                        println("remotes $remotes")
                        val locals = mutableMapOf<String, Local>()
                        getAllLocal().map { locals[it.key] = it }
                        locals.sync(remotes)
                    }
                }

            }
        }

    private suspend fun MutableMap<String, Local>.sync(remotes: List<Remote>) {
        val locals = this
        val saveLocalList = mutableListOf<Local>()
        val deleteLocalList = mutableListOf<Local>()

        val saveRemoteList = mutableListOf<Local>()
        val deleteRemoteList = mutableListOf<Remote>()

        remotes.map { remote ->
            val remoteKey = remote.key ?: "-"

            if (locals.containsKey(remoteKey)) {
                locals.getValue(remoteKey).let { local ->
                    val remoteSyncAt = remote.syncAt ?: 0

                    if (local.syncAt < remoteSyncAt) {
                        remote.toLocal(local)?.let { saveLocalList.add(it) }
                    } else if (!local.isSync) {
                        if (local.isDelete) {
                            deleteRemoteList.add(local.toRemote(remote))
                            deleteLocalList.add(local)
                        } else {
                            saveRemoteList.add(local)
                        }
                    } else if (local.syncAt > remoteSyncAt) {
                        saveRemoteList.add(local)
                    }

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
        if (saveRemoteList.isNotEmpty())
            withContext(coroutineContext) { launch { saveAllRemote(saveRemoteList) } }
        if (deleteRemoteList.isNotEmpty())
            withContext(coroutineContext) { launch { deleteAllRemote(deleteRemoteList) } }
    }
}