package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.WithdrawPermissionObject

@Dao
interface WithdrawPermissionDao : GrapheneObjectDao<WithdrawPermissionObject> {

    @Query("SELECT * FROM ${WithdrawPermissionObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): WithdrawPermissionObject?

    @Query("SELECT * FROM ${WithdrawPermissionObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<WithdrawPermissionObject?>

    @Query("DELETE FROM ${WithdrawPermissionObject.TABLE_NAME}")
    override suspend fun clear()

}