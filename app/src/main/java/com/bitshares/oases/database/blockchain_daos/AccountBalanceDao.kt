package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.AccountBalanceObject
import bitshareskit.objects.GrapheneObject

@Dao
interface AccountBalanceDao : GrapheneObjectDao<AccountBalanceObject>, OwnerQuery<AccountBalanceObject> {

    @Query("SELECT * FROM ${AccountBalanceObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): AccountBalanceObject?

    @Query("SELECT * FROM ${AccountBalanceObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<AccountBalanceObject?>

    @Query("DELETE FROM ${AccountBalanceObject.TABLE_NAME}")
    override suspend fun clear()

    @Query("SELECT * FROM ${AccountBalanceObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    override suspend fun getListByOwner(uid: Long): List<AccountBalanceObject>

    @Query("SELECT * FROM ${AccountBalanceObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    override fun getListByOwnerLive(uid: Long): LiveData<List<AccountBalanceObject>>

    @Query("SELECT * FROM ${AccountBalanceObject.TABLE_NAME} WHERE asset_uid = :uid")
    suspend fun getByAsset(uid: Long): AccountBalanceObject?

    @Query("SELECT * FROM ${AccountBalanceObject.TABLE_NAME} WHERE asset_uid = :uid")
    fun getByAssetLive(uid: Long): LiveData<AccountBalanceObject?>

}