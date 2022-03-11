package com.bitshares.oases.database.local_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bitshares.oases.database.entities.TickerEntity

@Dao
interface TickerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(single: TickerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(list: Collection<TickerEntity>)

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun addIgnore(single: TickerEntity)
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun addIgnore(list: Collection<TickerEntity>)

    @Query("SELECT * FROM ${TickerEntity.TABLE_NAME} WHERE ${TickerEntity.COLUMN_UID} = :uid")
    suspend fun get(uid: Long): TickerEntity?

    @Query("SELECT * FROM ${TickerEntity.TABLE_NAME} WHERE ${TickerEntity.COLUMN_UID} IN (:uids)")
    suspend fun get(uids: Set<Long>): TickerEntity?

    @Query("DELETE FROM ${TickerEntity.TABLE_NAME}")
    suspend fun clear()

    @Query("SELECT * FROM ${TickerEntity.TABLE_NAME} WHERE ${TickerEntity.COLUMN_UID} = :uid")
    fun getLive(uid: Long): LiveData<TickerEntity>

    @Query("SELECT * FROM ${TickerEntity.TABLE_NAME} WHERE ${TickerEntity.COLUMN_UID} IN (:uids)")
    fun getLive(uids: List<Long>): LiveData<List<TickerEntity>>

}