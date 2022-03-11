package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.TransactionObject

@Dao
interface TransactionDao : GrapheneObjectDao<TransactionObject> {

    @Query("SELECT * FROM ${TransactionObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): TransactionObject?

    @Query("SELECT * FROM ${TransactionObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<TransactionObject?>

    @Query("DELETE FROM ${TransactionObject.TABLE_NAME}")
    override suspend fun clear()

}