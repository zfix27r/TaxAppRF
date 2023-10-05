package com.taxapprf.data.sync

/*class SyncTransactions(
    private val localDao: LocalSyncDao,
    private val remoteDao: RemoteTransactionDao,
    private val transactionTypes: List<LocalTransactionTypeEntity>,
    private val currencies: List<LocalCBRCurrencyEntity>,
) : SyncManager<GetSyncTransactionModel, LocalTransactionEntity, FirebaseTransactionModel>() {
    private var accountId: Int? = null
    private var reportId: Int? = null

    private var accountKey: String? = null
    private var reportKey: String? = null

    suspend fun sync(
        accountId: Int,
        reportId: Int,
        accountKey: String,
        reportKey: String
    ) {
        this.accountId = accountId
        this.reportId = reportId
        this.accountKey = accountKey
        this.reportKey = reportKey

        startSync()
    }

    override fun GetSyncTransactionModel.toRemote(remote: FirebaseTransactionModel?): FirebaseTransactionModel {
        val firebaseTransactionModel =
            FirebaseTransactionModel(
                name,
                date,
                typeName,
                currencyCharCode,
                currencyRate,
                sum,
                tax,
                syncAt
            )
        firebaseTransactionModel.key = remoteKey
        return firebaseTransactionModel
    }

    override fun FirebaseTransactionModel.toLocal(local: GetSyncTransactionModel?): LocalTransactionEntity? {
        val reportId = reportId ?: return null
        val transactionKey = key ?: return null
        val date = date ?: return null
        val typeId = type?.findTypeId() ?: return null
        val currencyId = currency?.findCurrencyId() ?: return null
        val sum = sum ?: return null
        val syncAt = syncAt ?: 0

        return LocalTransactionEntity(
            id = local?.transactionId ?: DEFAULT_ID,
            reportId = reportId,
            typeOrdinal = typeId,
            currencyOrdinal = currencyId,
            name,
            date,
            sum,
            tax,
            remoteKey = transactionKey,
            syncAt = syncAt
        )
    }

    override fun getLocalList() =
        reportId?.let { localDao.getTransactions(it) } ?: emptyList()

    override fun deleteLocalList(locals: List<LocalTransactionEntity>): Int =
        localDao.deleteTransactions(locals)

    override fun saveLocalList(locals: List<LocalTransactionEntity>): List<Long> =
        localDao.saveTransactions(locals)

    override suspend fun getRemoteList() =
        if (accountKey != null && reportKey != null)
            remoteDao.getAll(accountKey!!, reportKey!!)
        else emptyList()

    override fun GetSyncTransactionModel.toLocalOut() =
        LocalTransactionEntity(
            id = transactionId,
            reportId = reportId ?: 0,
            typeOrdinal = typeId,
            currencyOrdinal = currencyId,
            name = name,
            date = date,
            sum = sum,
            tax = tax,
            remoteKey = remoteKey,
            syncAt = syncAt
        )

    override suspend fun GetSyncTransactionModel.updateRemoteKey() =
        if (accountKey != null && reportKey != null)
            remoteDao.getKey(accountKey!!, reportKey!!)?.let { copy(remoteKey = it) }
        else null

    override suspend fun updateRemoteList(remoteMap: Map<String, FirebaseTransactionModel?>) {
        if (accountKey != null && reportKey != null)
            remoteDao.updateAll(accountKey!!, reportKey!!, remoteMap)
    }

    private fun String.findTypeId() =
        transactionTypes.find { it.name == this }?.id

    private fun String.findCurrencyId() =
        currencies.find { it.name == this }?.id
}*/
