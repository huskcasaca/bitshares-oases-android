package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.WorkerObject

@Dao
interface WorkerDao : GrapheneObjectDao<WorkerObject>, OwnerQuery<WorkerObject> {

    @Query("SELECT * FROM ${WorkerObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): WorkerObject?

    @Query("SELECT * FROM ${WorkerObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<WorkerObject?>

    @Query("DELETE FROM ${WorkerObject.TABLE_NAME}")
    override suspend fun clear()

    @Query("SELECT * FROM ${WorkerObject.TABLE_NAME}")
    suspend fun getList(): List<WorkerObject>

    @Query("SELECT * FROM ${WorkerObject.TABLE_NAME}")
    fun getListLive(): LiveData<List<WorkerObject>>

    @Query("SELECT * FROM ${WorkerObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    override suspend fun getListByOwner(uid: Long): List<WorkerObject>

    @Query("SELECT * FROM ${WorkerObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    override fun getListByOwnerLive(uid: Long): LiveData<List<WorkerObject>>

}