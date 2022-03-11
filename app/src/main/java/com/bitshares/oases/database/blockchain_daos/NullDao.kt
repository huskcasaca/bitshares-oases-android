package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.NullObject

@Dao
interface NullDao : GrapheneObjectDao<NullObject> {

    @Query("SELECT * FROM ${NullObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): NullObject?

    @Query("SELECT * FROM ${NullObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<NullObject?>

    @Query("DELETE FROM ${NullObject.TABLE_NAME}")
    override suspend fun clear()


}