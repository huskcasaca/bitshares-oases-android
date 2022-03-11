package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.BucketObject
import bitshareskit.objects.GrapheneObject

@Dao
interface BucketDao : GrapheneObjectDao<BucketObject> {

    @Query("SELECT * FROM ${BucketObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): BucketObject?

    @Query("SELECT * FROM ${BucketObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<BucketObject?>

    @Query("DELETE FROM ${BucketObject.TABLE_NAME}")
    override suspend fun clear()


}