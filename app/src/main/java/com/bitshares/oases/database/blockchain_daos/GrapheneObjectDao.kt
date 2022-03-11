package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import bitshareskit.objects.GrapheneObject

interface GrapheneObjectDao<T : GrapheneObject> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(single: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(list: List<@JvmSuppressWildcards T>)

    suspend fun get(uid: Long): T?

    fun getLive(uid: Long): LiveData<T?>

    @Delete
    suspend fun remove(single: T)

    @Delete
    suspend fun remove(list: List<@JvmSuppressWildcards T>)

    suspend fun clear()
}