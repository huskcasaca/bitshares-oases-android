package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.BaseObject
import bitshareskit.objects.GrapheneObject

@Dao
interface BaseDao : GrapheneObjectDao<BaseObject> {

    @Query("SELECT * FROM ${BaseObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): BaseObject?

    @Query("SELECT * FROM ${BaseObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<BaseObject?>

    @Query("DELETE FROM ${BaseObject.TABLE_NAME}")
    override suspend fun clear()


}