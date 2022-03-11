package com.bitshares.oases.database.blockchain_daos


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.DynamicGlobalPropertyObject
import bitshareskit.objects.GrapheneObject

@Dao
interface DynamicGlobalPropertyDao : GrapheneObjectDao<DynamicGlobalPropertyObject> {

    @Query("SELECT * FROM ${DynamicGlobalPropertyObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): DynamicGlobalPropertyObject?

    @Query("SELECT * FROM ${DynamicGlobalPropertyObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<DynamicGlobalPropertyObject?>

    @Query("DELETE FROM ${DynamicGlobalPropertyObject.TABLE_NAME}")
    override suspend fun clear()

}