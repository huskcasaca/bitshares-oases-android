package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.HtlcObject

@Dao
interface HtlcDao : GrapheneObjectDao<HtlcObject> {

    @Query("SELECT * FROM ${HtlcObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): HtlcObject?

    @Query("SELECT * FROM ${HtlcObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<HtlcObject?>

    @Query("DELETE FROM ${HtlcObject.TABLE_NAME}")
    override suspend fun clear()

}