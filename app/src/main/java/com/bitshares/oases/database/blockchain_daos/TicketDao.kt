package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.TicketObject

@Dao
interface TicketDao : GrapheneObjectDao<TicketObject> {

    @Query("SELECT * FROM ${TicketObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): TicketObject?

    @Query("SELECT * FROM ${TicketObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<TicketObject?>

    @Query("DELETE FROM ${TicketObject.TABLE_NAME}")
    override suspend fun clear()


}