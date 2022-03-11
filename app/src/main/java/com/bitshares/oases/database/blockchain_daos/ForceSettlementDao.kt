package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.ForceSettlementObject
import bitshareskit.objects.GrapheneObject

@Dao
interface ForceSettlementDao : GrapheneObjectDao<ForceSettlementObject> {

    @Query("SELECT * FROM ${ForceSettlementObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): ForceSettlementObject?

    @Query("SELECT * FROM ${ForceSettlementObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<ForceSettlementObject?>

    @Query("DELETE FROM ${ForceSettlementObject.TABLE_NAME}")
    override suspend fun clear()

}