package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.WitnessScheduleObject

@Dao
interface WitnessScheduleDao : GrapheneObjectDao<WitnessScheduleObject> {

    @Query("SELECT * FROM ${WitnessScheduleObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): WitnessScheduleObject?

    @Query("SELECT * FROM ${WitnessScheduleObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<WitnessScheduleObject?>

    @Query("DELETE FROM ${WitnessScheduleObject.TABLE_NAME}")
    override suspend fun clear()

}