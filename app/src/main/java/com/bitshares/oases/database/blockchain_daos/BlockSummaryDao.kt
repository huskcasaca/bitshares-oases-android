package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.BlockSummaryObject
import bitshareskit.objects.GrapheneObject

@Dao
interface BlockSummaryDao : GrapheneObjectDao<BlockSummaryObject> {

    @Query("SELECT * FROM ${BlockSummaryObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): BlockSummaryObject?

    @Query("SELECT * FROM ${BlockSummaryObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<BlockSummaryObject?>

    @Query("DELETE FROM ${BlockSummaryObject.TABLE_NAME}")
    override suspend fun clear()

}