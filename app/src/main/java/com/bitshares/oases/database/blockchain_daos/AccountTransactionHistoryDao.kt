package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.AccountTransactionHistoryObject
import bitshareskit.objects.GrapheneObject

@Dao
interface AccountTransactionHistoryDao : GrapheneObjectDao<AccountTransactionHistoryObject> {

    @Query("SELECT * FROM ${AccountTransactionHistoryObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): AccountTransactionHistoryObject?

    @Query("SELECT * FROM ${AccountTransactionHistoryObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<AccountTransactionHistoryObject?>

    @Query("DELETE FROM ${AccountTransactionHistoryObject.TABLE_NAME}")
    override suspend fun clear()

}