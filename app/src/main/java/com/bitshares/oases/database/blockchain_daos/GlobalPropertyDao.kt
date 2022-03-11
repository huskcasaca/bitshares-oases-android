package com.bitshares.oases.database.blockchain_daos


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GlobalPropertyObject
import bitshareskit.objects.GrapheneObject

@Dao
interface GlobalPropertyDao : GrapheneObjectDao<GlobalPropertyObject> {

    @Query("SELECT * FROM ${GlobalPropertyObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): GlobalPropertyObject?

    @Query("SELECT * FROM ${GlobalPropertyObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<GlobalPropertyObject?>

    @Query("DELETE FROM ${GlobalPropertyObject.TABLE_NAME}")
    override suspend fun clear()

}