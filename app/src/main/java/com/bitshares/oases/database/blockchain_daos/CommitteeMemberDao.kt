package com.bitshares.oases.database.blockchain_daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import bitshareskit.objects.CommitteeMemberObject
import bitshareskit.objects.GrapheneObject

@Dao
interface CommitteeMemberDao : GrapheneObjectDao<CommitteeMemberObject>, OwnerQuery<CommitteeMemberObject> {

    @Query("SELECT * FROM ${CommitteeMemberObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override suspend fun get(uid: Long): CommitteeMemberObject?

    @Query("SELECT * FROM ${CommitteeMemberObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_UID} = :uid")
    override fun getLive(uid: Long): LiveData<CommitteeMemberObject?>

    @Query("DELETE FROM ${CommitteeMemberObject.TABLE_NAME}")
    override suspend fun clear()

    @Query("SELECT * FROM ${CommitteeMemberObject.TABLE_NAME}")
    suspend fun getList(): List<CommitteeMemberObject>

    @Query("SELECT * FROM ${CommitteeMemberObject.TABLE_NAME}")
    fun getListLive(): LiveData<List<CommitteeMemberObject>>

    @Query("SELECT * FROM ${CommitteeMemberObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    override suspend fun getListByOwner(uid: Long): List<CommitteeMemberObject>

    @Query("SELECT * FROM ${CommitteeMemberObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    override fun getListByOwnerLive(uid: Long): LiveData<List<CommitteeMemberObject>>

    @Query("SELECT * FROM ${CommitteeMemberObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    suspend fun getByOwner(uid: Long): CommitteeMemberObject?

    @Query("SELECT * FROM ${CommitteeMemberObject.TABLE_NAME} WHERE ${GrapheneObject.COLUMN_OWNER_UID} = :uid")
    fun getByOwnerLive(uid: Long): LiveData<CommitteeMemberObject?>


}