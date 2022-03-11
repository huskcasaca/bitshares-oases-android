package com.bitshares.oases.database.blockchain_daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bitshareskit.entities.Block

@Dao
interface BlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(single: Block)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(list: List<Block>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addIgnore(single: Block)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addIgnore(list: Collection<Block>)

    @Query("SELECT * FROM ${Block.TABLE_NAME} WHERE ${Block.COLUMN_HEIGHT} = :height")
    suspend fun get(height: Long): Block?

//    @Query("SELECT * FROM ${Block.TABLE_NAME} WHERE ${Block.COLUMN_HEIGHT} = :uid")
//    fun getLive(uid: Long): LiveData<AccountObject?>

    @Query("DELETE FROM ${Block.TABLE_NAME}")
    suspend fun clear()


}