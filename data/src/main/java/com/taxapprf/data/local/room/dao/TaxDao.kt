package com.taxapprf.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.TaxEntity
import com.taxapprf.data.local.room.model.DeleteTaxDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaxDao {
    @Query("SELECT * FROM tax WHERE account = :account")
    fun getTaxes(account: String): Flow<List<TaxEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTax(taxEntity: TaxEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTaxes(taxEntities: List<TaxEntity>)

    @Delete(entity = TaxEntity::class)
    fun deleteTaxes(deleteTaxDataModel: DeleteTaxDataModel)

    @Query("DELETE FROM tax")
    fun dropTaxes()
}