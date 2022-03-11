package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.SpecialAuthorityObject

@Dao
interface SpecialAuthorityDao : GrapheneObjectDao<SpecialAuthorityObject> {

    @Query("SELECT * FROM ${SpecialAuthorityObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): SpecialAuthorityObject?

    @Query("SELECT * FROM ${SpecialAuthorityObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<SpecialAuthorityObject?>

    @Query("DELETE FROM ${SpecialAuthorityObject.TABLE_NAME}")
    override suspend fun clear()

}