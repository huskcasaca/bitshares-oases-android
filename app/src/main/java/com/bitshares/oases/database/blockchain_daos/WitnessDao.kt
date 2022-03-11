package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.WitnessObject

@Dao
interface WitnessDao : GrapheneObjectDao<WitnessObject>, OwnerQuery<WitnessObject> {

    @Query("SELECT * FROM ${WitnessObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): WitnessObject?

    @Query("SELECT * FROM ${WitnessObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<WitnessObject?>

    @Query("DELETE FROM ${WitnessObject.TABLE_NAME}")
    override suspend fun clear()

    @Query("SELECT * FROM ${WitnessObject.TABLE_NAME}")
    suspend fun getList(): List<WitnessObject>

    @Query("SELECT * FROM ${WitnessObject.TABLE_NAME}")
    fun getListLive(): LiveData<List<WitnessObject>>

    @Query("SELECT * FROM ${WitnessObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    override suspend fun getListByOwner(uid: Long): List<WitnessObject>

    @Query("SELECT * FROM ${WitnessObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    override fun getListByOwnerLive(uid: Long): LiveData<List<WitnessObject>>

    @Query("SELECT * FROM ${WitnessObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    suspend fun getByOwner(uid: Long): WitnessObject?

    @Query("SELECT * FROM ${WitnessObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    fun getByOwnerLive(uid: Long): LiveData<WitnessObject?>

}