package com.bitshares.oases.provider.chain_repo

import androidx.lifecycle.LiveData
import bitshareskit.objects.CommitteeMemberObject
import com.bitshares.oases.chain.CommitteeMember
import com.bitshares.oases.database.BlockchainDatabase
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import bitshareskit.chain.CallMethod

object CommitteeMemberRepository {

    private val committeeMemberDao = BlockchainDatabase.INSTANCE.committeeMemberDao()

    val list: LiveData<List<CommitteeMemberObject>> = committeeMemberDao.getListLive()

    suspend fun getCommitteeMemberObject(uid: Long) = GrapheneRepository.getObject<CommitteeMemberObject>(uid)

    suspend fun getCommitteeCount(): Int {
        return NetworkService.sendOrNull(CallMethod.GET_WITNESS_COUNT) {
            runCatching { it as Int }.getOrDefault(0)
        }
    }

    suspend fun getAllCommittees(): List<CommitteeMemberObject>? = GrapheneRepository.getObjectFromChain((0L..getCommitteeCount()).toList())

    fun getByOwnerLive(uid: Long): LiveData<CommitteeMemberObject?> = committeeMemberDao.getByOwnerLive(uid)

    suspend fun getCommitteeDetail(committee: CommitteeMemberObject): CommitteeMemberObject = GrapheneRepository.getObject(committee)

    suspend fun getCommitteeMember(instance: CommitteeMemberObject) = CommitteeMember(instance, GrapheneRepository.getObject(instance.committeeMemberAccount))
    suspend fun getCommitteeMember(uid: Long) = getCommitteeMember(GrapheneRepository.getObjectOrEmpty(uid))

}

