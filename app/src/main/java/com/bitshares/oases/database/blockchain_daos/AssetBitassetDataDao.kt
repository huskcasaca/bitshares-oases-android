package com.bitshares.oases.database.blockchain_daos


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.AssetBitassetData
import bitshareskit.objects.GrapheneObject

@Dao
interface AssetBitassetDataDao : GrapheneObjectDao<AssetBitassetData> {

    @Query("SELECT * FROM ${AssetBitassetData.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): AssetBitassetData?

    @Query("SELECT * FROM ${AssetBitassetData.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<AssetBitassetData?>

    @Query("DELETE FROM ${AssetBitassetData.TABLE_NAME}")
    override suspend fun clear()

    @Query("SELECT * FROM ${AssetBitassetData.TABLE_NAME} WHERE asset_uid = :uid")
    suspend fun getByAsset(uid: Long): AssetBitassetData?

    @Query("SELECT * FROM ${AssetBitassetData.TABLE_NAME} WHERE asset_uid = :uid")
    fun getByAssetLive(uid: Long): LiveData<AssetBitassetData?>

}