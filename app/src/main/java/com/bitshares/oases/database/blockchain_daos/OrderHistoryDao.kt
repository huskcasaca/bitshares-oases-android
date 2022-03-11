package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.OrderHistoryObject

@Dao
interface OrderHistoryDao : GrapheneObjectDao<OrderHistoryObject> {

    @Query("SELECT * FROM ${OrderHistoryObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): OrderHistoryObject?

    @Query("SELECT * FROM ${OrderHistoryObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<OrderHistoryObject?>

    @Query("DELETE FROM ${OrderHistoryObject.TABLE_NAME}")
    override suspend fun clear()


}