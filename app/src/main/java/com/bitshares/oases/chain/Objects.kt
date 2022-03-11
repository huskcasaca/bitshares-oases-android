package com.bitshares.oases.chain

import bitshareskit.objects.AccountObject
import bitshareskit.objects.CommitteeMemberObject

data class CommitteeMember(
    val committee: CommitteeMemberObject,
    var account: AccountObject = committee.committeeMemberAccount,
) {
    companion object {
        val EMPTY = CommitteeMember(CommitteeMemberObject.EMPTY, AccountObject.EMPTY)
    }
}