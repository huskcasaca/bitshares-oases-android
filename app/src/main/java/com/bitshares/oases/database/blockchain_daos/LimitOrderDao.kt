package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.LimitOrderObject

@Dao
interface LimitOrderDao : GrapheneObjectDao<LimitOrderObject> {

    @Query("SELECT * FROM ${LimitOrderObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): LimitOrderObject?

    @Query("SELECT * FROM ${LimitOrderObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<LimitOrderObject?>

    @Query("DELETE FROM ${LimitOrderObject.TABLE_NAME}")
    override suspend fun clear()

}