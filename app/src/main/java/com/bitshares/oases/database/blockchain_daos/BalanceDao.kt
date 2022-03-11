package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.BalanceObject
import bitshareskit.objects.GrapheneObject

@Dao
interface BalanceDao : GrapheneObjectDao<BalanceObject> {

    @Query("SELECT * FROM ${BalanceObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): BalanceObject?

    @Query("SELECT * FROM ${BalanceObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<BalanceObject?>

    @Query("DELETE FROM ${BalanceObject.TABLE_NAME}")
    override suspend fun clear()

}