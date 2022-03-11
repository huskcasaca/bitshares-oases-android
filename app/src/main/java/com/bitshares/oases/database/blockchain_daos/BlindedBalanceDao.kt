package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.BlindedBalanceObject
import bitshareskit.objects.GrapheneObject

@Dao
interface BlindedBalanceDao : GrapheneObjectDao<BlindedBalanceObject> {

    @Query("SELECT * FROM ${BlindedBalanceObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): BlindedBalanceObject?

    @Query("SELECT * FROM ${BlindedBalanceObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<BlindedBalanceObject?>

    @Query("DELETE FROM ${BlindedBalanceObject.TABLE_NAME}")
    override suspend fun clear()

}