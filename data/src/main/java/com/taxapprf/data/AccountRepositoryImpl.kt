package com.taxapprf.data

import com.taxapprf.data.local.dao.AccountDao
import com.taxapprf.data.local.entity.AccountEntity
import com.taxapprf.domain.AccountRepository
import com.taxapprf.domain.user.AccountModel
import com.taxapprf.domain.user.SaveAccountModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val dao: AccountDao
) : AccountRepository {
    override fun getAccounts() = dao.getAccounts().map { it.toAccountModel() }

    override fun saveAccount(saveAccountModel: SaveAccountModel) = flow {
        if (saveAccountModel.active) dao.resetActiveAccount()
        dao.saveAccount(saveAccountModel.toAccountEntity())
        emit(Unit)
    }

    private fun SaveAccountModel.toAccountEntity() =
        AccountEntity(name = name.trim(), active = active)

    private fun List<AccountEntity>.toAccountModel() = map { AccountModel(it.name, it.active) }
}