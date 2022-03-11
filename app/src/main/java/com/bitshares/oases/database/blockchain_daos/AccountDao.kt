package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.AccountObject
import bitshareskit.objects.GrapheneObject

@Dao
interface AccountDao : GrapheneObjectDao<AccountObject> {

    @Query("SELECT * FROM ${AccountObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): AccountObject?

    @Query("SELECT * FROM ${AccountObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<AccountObject?>

    @Query("DELETE FROM ${AccountObject.TABLE_NAME}")
    override suspend fun clear()


}