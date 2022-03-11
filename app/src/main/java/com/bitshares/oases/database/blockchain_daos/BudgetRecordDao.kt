package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.BudgetRecordObject
import bitshareskit.objects.GrapheneObject

@Dao
interface BudgetRecordDao : GrapheneObjectDao<BudgetRecordObject> {

    @Query("SELECT * FROM ${BudgetRecordObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): BudgetRecordObject?

    @Query("SELECT * FROM ${BudgetRecordObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<BudgetRecordObject?>

    @Query("DELETE FROM ${BudgetRecordObject.TABLE_NAME}")
    override suspend fun clear()

    @Query("SELECT * FROM ${BudgetRecordObject.TABLE_NAME}")
    suspend fun getList(): List<BudgetRecordObject>

    @Query("SELECT * FROM ${BudgetRecordObject.TABLE_NAME}")
    fun getListLive(): LiveData<List<BudgetRecordObject>>

    @Query("SELECT * FROM ${BudgetRecordObject.TABLE_NAME} ORDER BY uid DESC LIMIT 1")
    suspend fun getLast(): BudgetRecordObject?

    @Query("SELECT * FROM ${BudgetRecordObject.TABLE_NAME} ORDER BY uid DESC LIMIT 1")
    fun getLastLive(): LiveData<BudgetRecordObject?>

//    @Query("SELECT * FROM ${BudgetRecordObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
//    override suspend fun getByOwner(uid: Long): List<BudgetRecordObject>
//
//    @Query("SELECT * FROM ${BudgetRecordObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
//    override fun getByOwnerLive(uid: Long): LiveData<List<BudgetRecordObject>>

}