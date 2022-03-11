package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.VestingBalanceObject

@Dao
interface VestingBalanceDao : GrapheneObjectDao<VestingBalanceObject> {

    @Query("SELECT * FROM ${VestingBalanceObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): VestingBalanceObject?

    @Query("SELECT * FROM ${VestingBalanceObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<VestingBalanceObject?>

    @Query("DELETE FROM ${VestingBalanceObject.TABLE_NAME}")
    override suspend fun clear()

}