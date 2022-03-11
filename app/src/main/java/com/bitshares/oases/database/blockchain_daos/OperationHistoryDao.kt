package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.OperationHistoryObject

@Dao
interface OperationHistoryDao : GrapheneObjectDao<OperationHistoryObject>, OwnerQuery<OperationHistoryObject> {

    @Query("SELECT * FROM ${OperationHistoryObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): OperationHistoryObject?

    @Query("SELECT * FROM ${OperationHistoryObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<OperationHistoryObject?>

    @Query("DELETE FROM ${OperationHistoryObject.TABLE_NAME}")
    override suspend fun clear()

    @Query("SELECT * FROM ${OperationHistoryObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    override suspend fun getListByOwner(uid: Long): List<OperationHistoryObject>

    @Query("SELECT * FROM ${OperationHistoryObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    override fun getListByOwnerLive(uid: Long): LiveData<List<OperationHistoryObject>>

}