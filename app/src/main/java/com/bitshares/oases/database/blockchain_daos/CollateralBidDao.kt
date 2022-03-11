package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.CollateralBidObject
import bitshareskit.objects.GrapheneObject

@Dao
interface CollateralBidDao : GrapheneObjectDao<CollateralBidObject> {

    @Query("SELECT * FROM ${CollateralBidObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): CollateralBidObject?

    @Query("SELECT * FROM ${CollateralBidObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<CollateralBidObject?>

    @Query("DELETE FROM ${CollateralBidObject.TABLE_NAME}")
    override suspend fun clear()


}