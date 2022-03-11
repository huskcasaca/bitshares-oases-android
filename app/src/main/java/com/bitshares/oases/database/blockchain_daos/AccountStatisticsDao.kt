package com.bitshares.oases.database.blockchain_daos


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.AccountStatisticsObject
import bitshareskit.objects.GrapheneObject

@Dao
interface AccountStatisticsDao : GrapheneObjectDao<AccountStatisticsObject> {

    @Query("SELECT * FROM ${AccountStatisticsObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): AccountStatisticsObject?

    @Query("SELECT * FROM ${AccountStatisticsObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<AccountStatisticsObject?>

    @Query("DELETE FROM ${AccountStatisticsObject.TABLE_NAME}")
    override suspend fun clear()


}