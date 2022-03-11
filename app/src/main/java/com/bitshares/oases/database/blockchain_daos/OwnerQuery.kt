package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import bitshareskit.objects.GrapheneObject

interface OwnerQuery<T : GrapheneObject> {

    suspend fun getListByOwner(uid: Long): List<@JvmSuppressWildcards T>

    fun getListByOwnerLive(uid: Long): LiveData<List<@JvmSuppressWildcards T>>
}