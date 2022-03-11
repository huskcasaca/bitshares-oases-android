package com.bitshares.oases.database.blockchain_daos


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.AssetDynamicData
import bitshareskit.objects.GrapheneObject

@Dao
interface AssetDynamicDataDao : GrapheneObjectDao<AssetDynamicData> {

    @Query("SELECT * FROM ${AssetDynamicData.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): AssetDynamicData?

    @Query("SELECT * FROM ${AssetDynamicData.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<AssetDynamicData?>

    @Query("DELETE FROM ${AssetDynamicData.TABLE_NAME}")
    override suspend fun clear()

}