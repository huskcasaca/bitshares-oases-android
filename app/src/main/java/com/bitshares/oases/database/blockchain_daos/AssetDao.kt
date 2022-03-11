package com.bitshares.oases.database.blockchain_daos


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.AssetObject
import bitshareskit.objects.GrapheneObject

@Dao
interface AssetDao : GrapheneObjectDao<AssetObject> {

    @Query("SELECT * FROM ${AssetObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): AssetObject?

    @Query("SELECT * FROM ${AssetObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<AssetObject?>

    @Query("DELETE FROM ${AssetObject.TABLE_NAME}")
    override suspend fun clear()

}