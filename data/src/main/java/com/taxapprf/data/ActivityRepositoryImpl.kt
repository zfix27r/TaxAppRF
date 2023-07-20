package com.taxapprf.data

import com.taxapprf.data.local.dao.ActivityDao
import com.taxapprf.data.local.entity.UserEntity
import com.taxapprf.data.remote.firebase.UserLivaData
import com.taxapprf.domain.ActivityRepository
import com.taxapprf.domain.activity.AccountModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ActivityRepositoryImpl @Inject constructor(
    private val dao: ActivityDao
) : ActivityRepository {
    override fun getAccountModel(): Flow<AccountModel?> {
        println("START")
        UserLivaData().firebaseUser?.let {
            println("FIREBASE USER")
            return flow { }
        }

        println("DATA USER CHECK")
        return dao.getUserModel()
            .map {
                if (it == null)
                    println("DATA USER NULL")
                else
                    println("DATA USER NOT NULL")
                it?.toUserModel()
            }
    }

    private fun UserEntity.toUserModel() = AccountModel(id, active, name, email)
}