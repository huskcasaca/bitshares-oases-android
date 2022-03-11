package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.FbaAccumulatorObject
import bitshareskit.objects.GrapheneObject

@Dao
interface FbaAccumulatorDao : GrapheneObjectDao<FbaAccumulatorObject> {

    @Query("SELECT * FROM ${FbaAccumulatorObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): FbaAccumulatorObject?

    @Query("SELECT * FROM ${FbaAccumulatorObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<FbaAccumulatorObject?>

    @Query("DELETE FROM ${FbaAccumulatorObject.TABLE_NAME}")
    override suspend fun clear()


}