package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.CustomAuthorityObject
import bitshareskit.objects.GrapheneObject

@Dao
interface CustomAuthorityDao : GrapheneObjectDao<CustomAuthorityObject> {

    @Query("SELECT * FROM ${CustomAuthorityObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): CustomAuthorityObject?

    @Query("SELECT * FROM ${CustomAuthorityObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<CustomAuthorityObject?>

    @Query("DELETE FROM ${CustomAuthorityObject.TABLE_NAME}")
    override suspend fun clear()

}