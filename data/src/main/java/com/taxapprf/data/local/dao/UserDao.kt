package com.taxapprf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.entity.UserEntity
import com.taxapprf.data.local.model.UserWithAccountModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query(
        "SELECT u.is_sign_in, u.name, u.email, u.phone, " +
                "a.name ${UserWithAccountModel.ACCOUNT_NAME}, " +
                "a.active ${UserWithAccountModel.ACCOUNT_ACTIVE} " +
                "FROM user u " +
                "LEFT JOIN account a ON a.active " +
                "WHERE u.is_sign_in = 1"
    )
    fun getSignIn(): Flow<UserWithAccountModel>

    @Query("UPDATE user SET is_sign_in = 1 WHERE name = :name")
    fun signIn(name: String)

    @Query("UPDATE user SET is_sign_in = 0")
    fun signOut()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userEntity: UserEntity): Long
}