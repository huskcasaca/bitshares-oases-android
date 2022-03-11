package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.ChainPropertyObject
import bitshareskit.objects.GrapheneObject

@Dao
interface ChainPropertyDao : GrapheneObjectDao<ChainPropertyObject> {

    @Query("SELECT * FROM ${ChainPropertyObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): ChainPropertyObject?

    @Query("SELECT * FROM ${ChainPropertyObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<ChainPropertyObject?>

    @Query("DELETE FROM ${ChainPropertyObject.TABLE_NAME}")
    override suspend fun clear()

}