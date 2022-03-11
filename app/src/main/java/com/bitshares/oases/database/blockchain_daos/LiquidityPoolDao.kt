package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.LiquidityPoolObject

@Dao
interface LiquidityPoolDao : GrapheneObjectDao<LiquidityPoolObject> {

    @Query("SELECT * FROM ${LiquidityPoolObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): LiquidityPoolObject?

    @Query("SELECT * FROM ${LiquidityPoolObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<LiquidityPoolObject?>

    @Query("DELETE FROM ${LiquidityPoolObject.TABLE_NAME}")
    override suspend fun clear()


}