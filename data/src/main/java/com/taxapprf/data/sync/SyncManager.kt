package com.taxapprf.data.sync

import com.taxapprf.domain.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

const val IS_SYNC = "is_sync"
const val IS_DELETE = "is_delete"
const val SYNC_AT = "sync_at"

abstract class SyncManager<Local : SyncLocal, Remote : SyncRemote, App>(
) {
    protected abstract fun observeLocal(): Flow<Local?>
    protected abstract fun observeAllLocal(): Flow<List<Local>>

    protected abstract fun getLocal(): Local?
    protected abstract fun getAllLocal(): List<Local>
    protected abstract fun getAllDeleteLocal(): List<Local>

    protected abstract fun Local.toRemote(remote: Remote? = null): Remote
    protected abstract fun Local.toApp(): App

    protected abstract fun saveAllLocal(locals: List<Local>)
    protected abstract fun deleteAllLocal(locals: List<Local>)

    protected abstract fun observeRemote(): Flow<Result<Remote?>>
    protected abstract fun observeAllRemote(): Flow<Result<List<Remote>>>

    protected abstract fun Remote.toLocal(local: Local? = null): Local?

    protected abstract suspend fun saveAllRemote(locales: List<Local>)
    protected abstract suspend fun deleteAllRemote(remotes: List<Remote>)

    protected fun String.isEmptyKey() = this == ""

    private val saveLocalList = mutableListOf<Local>()
    private val deleteLocalList = mutableListOf<Local>()

    private val saveRemoteList = mutableListOf<Local>()
    private val deleteRemoteList = mutableListOf<Remote>()

    private var isLock = false

    fun observe() =
        channelFlow {
            val locals = mutableMapOf<String, Local>()

            launch(Dispatchers.IO) {
                observeLocal().collectLatest { newLocal ->
                    send(newLocal?.toApp())
                    locals.update(newLocal)
                }
            }

            launch(Dispatchers.IO) {
                observeRemote().collectLatest { result ->
                    result.getOrNull()?.let { remote ->
                        val remotes = listOf(remote)
                        locals.sync(remotes)
                    }
                }
            }
        }

    fun observeAll() =
        channelFlow {
            launch(Dispatchers.IO) {
                observeAllLocal().collectLatest { newLocals ->
                    println("newLocals $newLocals")
                    send(newLocals.map { it.toApp() })
                }
            }

            launch(Dispatchers.IO) {
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

    private fun deleteLocalAndRemote(local: Local, remote: Remote?) {
        deleteRemoteList.add(local.toRemote(remote))
        deleteLocalList.add(local)
    }

    private fun MutableMap<String, Local>.update(local: Local?) {
        local?.let { this[it.key] = it }
    }

    private fun MutableMap<String, Local>.update(locals: List<Local>) {
        locals.map { this[it.key] = it }
    }
}