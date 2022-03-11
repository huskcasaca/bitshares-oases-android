package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.ProposalObject

@Dao
interface ProposalDao : GrapheneObjectDao<ProposalObject> {

    @Query("SELECT * FROM ${ProposalObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): ProposalObject?

    @Query("SELECT * FROM ${ProposalObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<ProposalObject?>

    @Query("DELETE FROM ${ProposalObject.TABLE_NAME}")
    override suspend fun clear()

}