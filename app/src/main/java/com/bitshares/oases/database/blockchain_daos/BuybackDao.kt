package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.BuybackObject
import bitshareskit.objects.GrapheneObject

@Dao
interface BuybackDao : GrapheneObjectDao<BuybackObject> {

    @Query("SELECT * FROM ${BuybackObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): BuybackObject?

    @Query("SELECT * FROM ${BuybackObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<BuybackObject?>

    @Query("DELETE FROM ${BuybackObject.TABLE_NAME}")
    override suspend fun clear()


}