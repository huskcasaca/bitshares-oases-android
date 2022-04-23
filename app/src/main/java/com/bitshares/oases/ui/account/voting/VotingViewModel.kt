package com.bitshares.oases.ui.account.voting

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import bitshareskit.extensions.isProxyToSelf
import bitshareskit.models.Vote
import bitshareskit.objects.AccountObject
import bitshareskit.operations.AccountUpdateOperation
import com.bitshares.oases.chain.CommitteeMember
import com.bitshares.oases.netowrk.java_websocket.TransactionBuilder
import com.bitshares.oases.netowrk.java_websocket.buildTransaction
import com.bitshares.oases.provider.chain_repo.*
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.main.explore.containsInternal
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.livedata.*

class VotingViewModel(application: Application) : AccountViewModel(application) {

    val viewPage = NonNullMutableLiveData(0)

    fun refresh() {
        viewModelScope.launch {
            listOf(
                async(coroutineContext) { CommitteeMemberRepository.getAllCommittees() },
                async(coroutineContext) { WitnessRepository.getAllWitnesses() },
                async(coroutineContext) { WorkerRepository.getAllWorkers() },
                async(coroutineContext) { ChainPropertyRepository.getLastGlobalProperty() },
                async(coroutineContext) { BudgetRecordRepository.getLastBudgetRecord() }
            ).awaitAll()
        }
    }

    private val committeeMemberList =
        CommitteeMemberRepository.list.mapChild { CommitteeMember(it) }.replaceChildAsync(viewModelScope, 20) {
//        delay(4000 + Random.nextLong(4) * 1000L)
            it.copy(account = GrapheneRepository.getObject(it.committee.committeeMemberAccount.uid) ?: it.committee.committeeMemberAccount)
        }.map { it.sortedByDescending { it.committee.totalVotes } }

    private val witnessList = WitnessRepository.list.replaceChildAsync(viewModelScope) {
//        delay(4000 + Random.nextLong(4) * 1000L)
        it.copy().apply { witnessAccount = GrapheneRepository.getObject(witnessAccount.uid) ?: witnessAccount }
    }.map { it.sortedByDescending { it.totalVotes } }

    val workerList = WorkerRepository.list.replaceChildAsync(viewModelScope) {
//        delay(4000 + Random.nextLong(4) * 1000L)
        it.copy().apply { workerAccount = GrapheneRepository.getObject(workerAccount.uid) ?: workerAccount }
    }.map { it.sortedByDescending { it.totalVotesFor - it.totalVotesAgainst } }

    private val activeCommitteeMemberUids =
        ChainPropertyRepository.globalProperty.map { it?.activeCommitteeMembers.orEmpty().map { committee -> committee.uid } }
            .distinctUntilChanged()
    private val activeWitnessUids =
        ChainPropertyRepository.globalProperty.map { it?.activeWitnesses.orEmpty().map { witness -> witness.uid } }.distinctUntilChanged()
    private val workerBudget = BudgetRecordRepository.lastBudgetRecord.map { it?.workerBudget?.times(24) }
    private val workerBudgetPerDay = ChainPropertyRepository.globalProperty.map { it?.workerBudgetPerDay }


    val filter = NonNullMutableLiveData(EMPTY_SPACE)

    // committee members
    val activeCommitteeMembers = combineLatest(committeeMemberList, activeCommitteeMemberUids) { all, active ->
        if (all.isNullOrEmpty() || active.isNullOrEmpty()) emptyList() else all.filter { active.contains(it.committee.uid) }
    }
    val standbyCommitteeMembers = combineLatest(committeeMemberList, activeCommitteeMemberUids) { all, active ->
        if (all.isNullOrEmpty() || active.isNullOrEmpty()) all.orEmpty() else all.filterNot { active.contains(it.committee.uid) }
    }
    val activeCommitteeMembersFiltered = combineNonNull(activeCommitteeMembers, filter) { all, filter ->
        all.filter { filter.isEmpty() || it.containsInternal(filter) }
    }
    val standbyCommitteeMembersFiltered = combineNonNull(standbyCommitteeMembers, filter) { all, filter ->
        all.filter { filter.isEmpty() || it.containsInternal(filter) }
    }

    // witnesses
    val activeWitnesses = combineLatest(witnessList, activeWitnessUids) { all, active ->
        if (all.isNullOrEmpty() || active.isNullOrEmpty()) emptyList() else all.filter { active.contains(it.uid) }
    }
    val standbyWitnesses = combineLatest(witnessList, activeWitnessUids) { all, active ->
        if (all.isNullOrEmpty() || active.isNullOrEmpty()) all.orEmpty() else all.filterNot { active.contains(it.uid) }
    }
    val activeWitnessesFiltered = combineNonNull(activeWitnesses, filter) { all, filter ->
        all.filter { filter.isEmpty() || it.containsInternal(filter) }
    }
    val standbyWitnessesFiltered = combineNonNull(standbyWitnesses, filter) { all, filter ->
        all.filter { filter.isEmpty() || it.containsInternal(filter) }
    }

    // workers
    val activeWorkersFiltered = combineLatest(workerList, workerBudgetPerDay, filter) { all, budget, filter ->
        when {
            all.isNullOrEmpty() || budget == null -> emptyList()
            else -> {
                var remainingBudget: Long = budget
                all.filter { remainingBudget -= it.dailyPay; remainingBudget >= 0 }
                    .filter { filter.isNullOrBlank() || it.containsInternal(filter) }
            }
        }
    }

    val standbyWorkerFiltered = combineLatest(workerList, workerBudgetPerDay, filter) { all, budget, filter ->
        if (all.isNullOrEmpty() || budget == null) all.orEmpty() else {
            var remainingBudget: Long = budget
            all.filter { remainingBudget -= it.dailyPay; remainingBudget < 0 }
                .filter { filter.isNullOrBlank() || it.containsInternal(filter) }
        }
    }

    val proxyTo = account.filterNotNull().map { it.options.votingAccount }.toMutableLiveData()

    val proxyEnabled = combineNonNull(account, proxyTo) { account, proxyTo ->
        proxyTo.uid != account.uid && proxyTo.uid != AccountObject.PROXY_TO_SELF_UID
    }

    val proxyToDetailed = proxyTo.filterNotNull().map(viewModelScope) { AccountRepository.getAccountDetail(it) }

    val accountVoting = account.mapSuspend {
        var times = 0
        var account = it
        while (account != null && account.options.votingAccount.id != AccountObject.PROXY_TO_SELF_ID && times++ < 5) {
            account = AccountRepository.getAccountObjectFromChain(account.uid)
        }
        account?.options?.vote.orEmpty()
    }.distinctUntilChanged()

    val voting: MutableLiveData<Set<Vote>> = accountVoting.toMutableLiveData()

//    private val committeeMemberWithState = combineLatest(committeeMemberList, activeCommitteeMemberUids) { all, active -> if (all.isNullOrEmpty() || active.isNullOrEmpty()) emptyList() else all.map { CommitteePayloads(it, active.contains(it.uid)) } }
//    val committeeMemberPayloads = combineNonNull(committeeMemberWithState, voting) { list, voting -> list.map { it.copy(isVoted = voting.contains(it.committee.vote)) }.shuffled() }
//
//    val activeCommitteeMemberPayloads = committeeMemberPayloads.filterChild { it.isActive }
//    val standbyCommitteeMemberPayloads = committeeMemberPayloads.filterChildNot { it.isActive }

    val activeCommitteeMemberVoted = combineNonNull(activeCommitteeMembers, voting) { list, voting ->
        list.map { voting.contains(it.committee.vote) }
    }
    val standbyCommitteeMemberVoted = combineNonNull(standbyCommitteeMembers, voting) { list, voting ->
        list.map { voting.contains(it.committee.vote) }
    }

    val activeWitnessVoted = combineNonNull(activeWitnesses, voting) { list, voting ->
        list.map { voting.contains(it.vote) }
    }
    val standbyWitnessVoted = combineNonNull(standbyWitnesses, voting) { list, voting ->
        list.map { voting.contains(it.vote) }
    }

    val activeWorkerVoted = combineNonNull(activeWorkersFiltered, voting) { list, voting ->
        list.map { voting.contains(it.voteFor) }
    }
    val standbyWorkerVoted = combineNonNull(standbyWorkerFiltered, voting) { list, voting ->
        list.map { voting.contains(it.voteFor) }
    }

    fun changeProxy(proxy: AccountObject) {
        proxyTo.value = proxy
        if (proxy.uid != AccountObject.PROXY_TO_SELF_UID && proxy.uid != account.value?.uid) {
            voting.value = account.value?.options?.vote.orEmpty()
        }
    }

    fun changeVote(vote: Vote, add: Boolean) {
        voting.value = if (add) (voting.value.orEmpty() + vote) else (voting.value.orEmpty() - vote)
    }

    fun changeVote(vote: Set<Vote>, add: Boolean) {
        voting.value = if (add) (voting.value.orEmpty() + vote) else (voting.value.orEmpty() - vote)
    }

    fun filterCommitteeList(votes: Set<Vote>) = committeeMemberList.value.orEmpty().filter { votes.contains(it.committee.vote) }
    fun filterWitnessList(votes: Set<Vote>) = witnessList.value.orEmpty().filter { votes.contains(it.vote) }
    fun filterWorkerList(votes: Set<Vote>) = workerList.value.orEmpty().filter { votes.contains(it.voteFor) }

    val transactionBuilder = MutableLiveData<TransactionBuilder>()
    val transaction = transactionBuilder.map { it.build() }
    val operation = transaction.map { it.operations.firstOrNull() as AccountUpdateOperation? }.filterNotNull()

    val isProxyModified = combineLatest(account, proxyTo) { account, proxyTo ->
        account != null && proxyTo != null &&
                if (account.uid == account.options.votingAccount.uid || account.options.votingAccount.isProxyToSelf()) {
                    !proxyTo.isProxyToSelf() && proxyTo.uid != account.uid
                } else {
                    proxyTo.uid != account.options.votingAccount.uid
                }
    }

    val isVoteListModified = combineLatest(account, voting) { account, voting ->
        account != null && account.options.vote != voting.orEmpty()
    }

    val isModified = combineBooleanAny(isProxyModified, isVoteListModified)
    fun isModified() = isModified.value ?: false

    fun buildTransaction(): TransactionBuilder = buildTransaction {
        addOperation {
            val account = account.value!!
            val proxyToOld =
                if (account.options.votingAccount.uid == account.uid) AccountObject.PROXY_TO_SELF else account.options.votingAccount
            val proxyToNew = proxyTo.value
            val proxyTo = proxyToNew ?: proxyToOld
            val votes = voting.value.orEmpty()
            val options = if (!proxyTo.isProxyToSelf() && proxyTo.uid != account.uid) {
                account.options.copy(
                    votingAccount = proxyTo,
                    witnessNumber = 0U,
                    committeeNumber = 0U,
                    vote = emptySet(),
                )
            } else {
                account.options.copy(
                    votingAccount = AccountObject.PROXY_TO_SELF,
                    witnessNumber = votes.count { it.group == Vote.WITNESS_GROUP }.toUShort(),
                    committeeNumber = votes.count { it.group == Vote.COMMITTEE_GROUP }.toUShort(),
                    vote = votes,
                )
            }
            AccountUpdateOperation(account, null, null, options)
        }
//        addKeys(AuthorityService.ownerRequiredAuths)
        transactionBuilder.value = this
        checkFees()
    }

}

