package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.CustomObject
import bitshareskit.objects.GrapheneObject

@Dao
interface CustomDao : GrapheneObjectDao<CustomObject> {

    @Query("SELECT * FROM ${CustomObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): CustomObject?

    @Query("SELECT * FROM ${CustomObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<CustomObject?>

    @Query("DELETE FROM ${CustomObject.TABLE_NAME}")
    override suspend fun clear()

}