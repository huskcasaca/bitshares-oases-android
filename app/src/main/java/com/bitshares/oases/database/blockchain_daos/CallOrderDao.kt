package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.CallOrderObject
import bitshareskit.objects.GrapheneObject

@Dao
interface CallOrderDao : GrapheneObjectDao<CallOrderObject> {

    @Query("SELECT * FROM ${CallOrderObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): CallOrderObject?

    @Query("SELECT * FROM ${CallOrderObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<CallOrderObject?>

    @Query("DELETE FROM ${CallOrderObject.TABLE_NAME}")
    override suspend fun clear()

}