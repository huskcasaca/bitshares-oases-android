package com.bitshares.oases.ui.account

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import bitshareskit.models.FullAccount
import bitshareskit.models.PrivateKey
import bitshareskit.models.PublicKey
import bitshareskit.objects.*
import com.bitshares.oases.chain.AccountBalance
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.chain.resolveAccountPath
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.chain_repo.*
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.ui.base.BaseViewModel
import com.bitshares.oases.ui.base.getJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.livedata.*
import java.math.BigDecimal

open class AccountViewModel(application: Application) : BaseViewModel(application) {

    val accountUid = NonNullMutableLiveData(ChainConfig.EMPTY_INSTANCE)

    val fullAccount: LiveData<FullAccount?> = accountUid.switchMap { AccountRepository.getFullAccountLive(it) }.distinctUntilChanged()

    val account: LiveData<AccountObject?> = combineFirst(accountUid.switchMap { AccountRepository.getAccountLiveFromDao(it) }, fullAccount) { account, _ -> account }.distinctUntilChanged().sources(fullAccount)
    val accountNonNull = account.filterNotNull()
    val accountStaticById = accountNonNull.distinctUntilChangedBy { it.uid }

    val accountName = account.map { it.nameOrEmpty }.distinctUntilChanged()

    val accountBalanceInternal: LiveData<List<AccountBalanceObject>> = accountUid.switchMap { AccountRepository.getAccountBalanceListLiveFromDao(it) }.filterChildNot { it.balance == 0L }.mapChildParallel(viewModelScope) { balance ->
        balance.apply { AssetRepository.getAssetWithBitassetData(asset.uid)?.let { balance.asset = it } }
    }

    val accountBalance = combineFirst(accountUid, accountBalanceInternal) { uid, balances -> balances.orEmpty().filter { it.ownerUid == uid } }
    val limitOrders = fullAccount.filterNotNull().map { it.limitOrders }.distinctUntilChanged()
    val callOrders = fullAccount.filterNotNull().map { it.callOrders }.distinctUntilChanged()


    val callOrdersExtended = callOrders.mapChildParallel(viewModelScope) { MarketRepository.getCallOrder(it) }

    // balance started

    val unit = Settings.KEY_BALANCE_UNIT.map(viewModelScope) { AssetRepository.getAssetOrDefault(it, Graphene.KEY_CORE_ASSET.value) }.distinctUntilChangedBy { it.uid }.filterNotNull()

    val accountBalanceWithOrders = combineNonNull(accountBalance, limitOrders, callOrders).map(viewModelScope) { (balances, limits, calls) ->
        balances.map { balance -> AccountBalance(balance, limits.filter { it.salePrice.base.asset.uid == balance.assetUid }, calls.filter { it.collateralAmount.asset.uid == balance.assetUid || it.debtAmount.asset.uid == balance.assetUid })  }
    }

    val accountBalanceWithPrices = combineNonNull(accountBalanceWithOrders, unit) { balances, unit ->
        balances.map { it to unit }
    }.mapChildParallel(viewModelScope) { (balance, unit) ->
        balance.copy(value = MarketRepository.getTickerLF(unit, balance.balance.asset).let {
            if (balance.balance.asset.isInstanceOf(unit)) balance.totalAmount else formatAssetAmount(if (it == null) BigDecimal.ZERO else balance.totalAmount.formattedValue * BigDecimal(it.highestBid), unit)
        })
    }

    val accountBalanceOnly = accountBalance.map(viewModelScope) { it.map { AccountBalance(it, emptyList(), emptyList()) } }

    val balanceCombined = combineFirst(accountBalanceOnly, accountBalanceWithOrders, accountBalanceWithPrices) { b1, b2, b3 -> (b3.takeIf { it.isNotNullOrEmpty() } ?: b2.takeIf { it.isNotNullOrEmpty() } ?: b1).orEmpty() }

    val balanceSorted = balanceCombined.map { it.sortedByDescending { it.value?.amount } }

    val totalAmount = combineNonNull(accountBalanceWithPrices, unit) { prices, unit -> formatAssetAmount(prices.sumOf { it.value?.amount ?: 0 }, unit) }


    // balance ended


    val limitOrdersDetailed = limitOrders.mapChildParallel(viewModelScope) { MarketRepository.getOrderDetail(it, it.rebasedMarket) }


    // map(viewModelScope) for better performance

    val accountStatistics: LiveData<AccountStatisticsObject> = accountUid.switchMap { AccountRepository.getAccountStatisticsLiveFromDao(it) }.filterNotNull().distinctUntilChangedBy { it.uid }
    val accountCommitteeInfo: LiveData<CommitteeMemberObject> = accountUid.switchMap { CommitteeMemberRepository.getByOwnerLive(it) }.filterNotNull().distinctUntilChangedBy { it.uid }
    val accountWitnessInfo: LiveData<WitnessObject> = accountUid.switchMap { WitnessRepository.getByOwnerLive(it) }.filterNotNull().distinctUntilChangedBy { it.uid }

    val accountVotes: LiveData<List<GrapheneObject>> = fullAccount.map { it?.votes.orEmpty() }.distinctChildUntilChangedBy { it.uid }
    val accountCommitteeMemberVotes = fullAccount.map { it?.committeeVotes.orEmpty() }.distinctChildUntilChangedBy { it.uid }.mapChildParallel(viewModelScope) { CommitteeMemberRepository.getCommitteeMember(it) }
    val accountWitnessVotes: LiveData<List<WitnessObject>> = fullAccount.map { it?.witnessVotes.orEmpty() }.distinctChildUntilChangedBy { it.uid }.onEachChildParallel(viewModelScope) { witnessAccount = AccountRepository.getAccountObject(witnessAccount.uid) ?: witnessAccount }
    val accountWorkerVotes: LiveData<List<WorkerObject>> = fullAccount.map { it?.workerVotes.orEmpty() }.distinctChildUntilChangedBy { it.uid }.onEachChildParallel(viewModelScope) { workerAccount = AccountRepository.getAccountObject(workerAccount.uid) ?: workerAccount }

    val votingAccount = account.filterNotNull().map(viewModelScope) { AccountRepository.getAccountObject(it.options.votingAccount.uid) }.filterNotNull()

    val blacklisted: LiveData<List<AccountObject>> = account.map { it?.blackListedAccount.orEmpty() }.distinctChildUntilChangedBy { it.uid }.mapChildParallel(viewModelScope) { AccountRepository.getAccountObject(it.uid) ?: it }
    val blacklisting: LiveData<List<AccountObject>> = account.map { it?.blackListingAccount.orEmpty() }.distinctChildUntilChangedBy { it.uid }.mapChildParallel(viewModelScope) { AccountRepository.getAccountObject(it.uid) ?: it }
    val whitelisted: LiveData<List<AccountObject>> = account.map { it?.whiteListedAccount.orEmpty() }.distinctChildUntilChangedBy { it.uid }.mapChildParallel(viewModelScope) { AccountRepository.getAccountObject(it.uid) ?: it }
    val whitelisting: LiveData<List<AccountObject>> = account.map { it?.whiteListingAccount.orEmpty() }.distinctChildUntilChangedBy { it.uid }.mapChildParallel(viewModelScope) { AccountRepository.getAccountObject(it.uid) ?: it }

    val registrar = accountNonNull.map { it.registrar?.uid }.filterNotNull().distinctUntilChanged().map(viewModelScope) { AccountRepository.getAccountObject(it) }.filterNotNull()
    val referrer = accountNonNull.map { it.referrer?.uid }.filterNotNull().distinctUntilChanged().map(viewModelScope) { AccountRepository.getAccountObject(it) }.filterNotNull()
    val lifetimeReferrer = accountNonNull.map { it.lifetimeReferrer?.uid }.filterNotNull().distinctUntilChanged().map(viewModelScope) { AccountRepository.getAccountObject(it) }.filterNotNull()
    val network = accountNonNull.map { AccountObject.NULL_ACCOUNT_UID }.filterNotNull().distinctUntilChanged().map(viewModelScope) { AccountRepository.getAccountObject(it) }.filterNotNull()

    val referrerFeePercentage = accountNonNull.map { it.referrerRewardsFeePercentage }
    val registerFeePercentage = emptyLiveData(0)
    val lifetimeReferrerFeePercentage = accountNonNull.map { it.lifetimeReferrerFeePercentage }
    val networkFeePercentage = accountNonNull.map { it.networkFeePercentage }

//    val ownerKeyAuths = account.map { it?.ownerKeyAuths.orEmpty() }.distinctUntilChanged()
//    val activeKeyAuths = account.map { it?.activeKeyAuths.orEmpty() }.distinctUntilChanged()
//    val memoKeyAuths = account.map { it?.memoKeyAuths.orEmpty() }.distinctUntilChanged()

    val ownerKeyAuths = accountNonNull.map { it.ownerKeyAuths }.distinctUntilChanged()
    val activeKeyAuths = accountNonNull.map { it.activeKeyAuths }.distinctUntilChanged()
    val memoKeyAuths = accountNonNull.map { it.memoKeyAuths }.distinctUntilChanged()

    val ownerMinThreshold = account.map { it?.ownerMinThreshold }.distinctUntilChanged()
    val activeMinThreshold = account.map { it?.activeMinThreshold }.distinctUntilChanged()
    val memoMinThreshold = account.map { it?.memoMinThreshold }.distinctUntilChanged()

    val ownerAccountAuths = account.map { it?.ownerAccountAuths.orEmpty().asIterable() }.mapChildParallel(viewModelScope) { (account, threshold) -> AccountRepository.getAccountObject(account.uid)?.let { it to threshold } ?: account to threshold }.map { it.toMap() }
    val activeAccountAuths = account.map { it?.activeAccountAuths.orEmpty().asIterable() }.mapChildParallel(viewModelScope) { (account, threshold) -> AccountRepository.getAccountObject(account.uid)?.let { it to threshold } ?: account to threshold }.map { it.toMap() }

    // local keys from AuthorityViewModel

    private val chainIdCurrent = Graphene.KEY_CHAIN_ID

    private val chainIdManual = MutableLiveData<String>()

    private val chainId = combineFirst(chainIdManual, chainIdCurrent) { manual, current -> manual ?: current }.filterNotNull()

    protected val userUid = NonNullMutableLiveData(ChainConfig.EMPTY_INSTANCE)
    private val userInternal = combineNonNull(userUid, chainId).switchMap { (userUid, chainId) -> LocalUserRepository.getUserLive(globalWalletManager, userUid, chainId) }
    val user = combineFirst(chainId, userInternal) { chainId, user -> if (user != null && chainId != null && user.chainId == chainId) user else null }.sources(account)

    val ownerKeyAuthsLocal = user.map { it?.ownerKeys.orEmpty() }.distinctUntilChanged()
    val activeKeyAuthsLocal = user.map { it?.activeKeys.orEmpty() }.distinctUntilChanged()
    val memoKeyAuthsLocal = user.map { it?.memoKeys.orEmpty() }.distinctUntilChanged()

    val currentUser = LocalUserRepository.decryptCurrentUser(globalWalletManager)

    val ownerRequiredAuths = combineFirst(ownerKeyAuths, ownerKeyAuthsLocal, ownerMinThreshold) { keyAuths, keyAuthsLocal, threshold -> createKeySet(keyAuths, keyAuthsLocal, threshold) }.withDefault { emptySet() }
    val activeRequiredAuths = combineFirst(activeKeyAuths, activeKeyAuthsLocal, activeMinThreshold) { keyAuths, keyAuthsLocal, threshold -> createKeySet(keyAuths, keyAuthsLocal, threshold) }.withDefault { emptySet() }
    val memoRequiredAuths = combineFirst(memoKeyAuths, memoKeyAuthsLocal, memoMinThreshold) { keyAuths, keyAuthsLocal, threshold -> createKeySet(keyAuths, keyAuthsLocal, 1U)  }.withDefault { emptySet() }

    val isOwnerAuthorized get() = checkSufficient(ownerKeyAuths.value, ownerKeyAuthsLocal.value, ownerMinThreshold.value)
    val isActiveAuthorized get() = checkSufficient(activeKeyAuths.value, activeKeyAuthsLocal.value, activeMinThreshold.value)
    val isMemoAuthorized get() = checkSufficient(memoKeyAuths.value, memoKeyAuthsLocal.value, memoMinThreshold.value)

    val isOwnerAuthorizedLive = combineFirst(ownerKeyAuths, ownerKeyAuthsLocal, ownerMinThreshold) { keyAuths, keyAuthsLocal, threshold -> checkSufficient(keyAuths, keyAuthsLocal, threshold) }.sources(ownerRequiredAuths)
    val isActiveAuthorizedLive = combineFirst(activeKeyAuths, activeKeyAuthsLocal, activeMinThreshold) { keyAuths, keyAuthsLocal, threshold -> checkSufficient(keyAuths, keyAuthsLocal, threshold) }.sources(activeRequiredAuths)
    val isMemoAuthorizedLive = combineFirst(memoKeyAuths, memoKeyAuthsLocal, memoMinThreshold) { keyAuths, keyAuthsLocal, threshold -> checkSufficient(keyAuths, keyAuthsLocal, 1U) }.sources(memoRequiredAuths)

    val isAuthorized = combineBooleanAll(isOwnerAuthorizedLive, isActiveAuthorizedLive, isMemoAuthorizedLive)

    private fun createKeySet(keyMap: Map<PublicKey, UShort>?, from: Set<PrivateKey>?, minimal: UInt?): Set<PrivateKey> {
        if (keyMap == null || from == null || minimal == null) return emptySet()
        val toLocal = mutableSetOf<PrivateKey>()
        var accumulated = 0U
        keyMap.toList().sortedByDescending { it.second }.forEach { (key, threshold) ->
            if (accumulated >= minimal) return@forEach
            from.find { it.publicKey == key }?.let {
                toLocal.add(it)
                accumulated += threshold
            }
        }
        return toLocal
    }


    fun checkSufficient(keyMap: Map<PublicKey, UShort>?, from: Set<PrivateKey>?, minimal: UInt?): Boolean {
        if (keyMap == null || from == null || minimal == null) return false
        var accumulated = 0U
        keyMap.toList().sortedByDescending { it.second }.forEach { (key, threshold) ->
            if (accumulated >= minimal) return@forEach
            from.find { it.publicKey == key }?.let { accumulated += threshold }
        }
        return accumulated >= minimal
    }




    // sort
    enum class BalanceSortMethod {
        EMPTY, SYMBOL, BALANCE, TYPE, VALUE
    }

    private val balanceSortOptions = NonNullMutableLiveData(BalanceSortMethod.EMPTY)

//    val accountBalanceSorted = combineLatest(accountBalance, balanceSortOptions) { balance, sort ->
//        if (balance == null || sort == null) return@combineLatest emptyList<AccountBalanceObject>()
//        when (sort) {
//            BalanceSortMethod.EMPTY -> balance.sortedBy { it.asset.symbol }
//            BalanceSortMethod.SYMBOL -> balance.sortedBy { it.asset.symbol }
//            BalanceSortMethod.BALANCE -> balance.sortedBy { it.balance }
//            BalanceSortMethod.TYPE -> balance.sortedBy { it.asset.assetType }
//            BalanceSortMethod.VALUE -> TODO()
//        }
//    }

    fun sortBalanceBy(method: BalanceSortMethod) {
        balanceSortOptions.value = method
    }

    val isAccountObservable = accountUid.switchMap { LocalUserRepository.getUserLive(globalWalletManager, it, ChainPropertyRepository.chainId) }.map { it == null }

    val activities = accountUid.switchMap { AccountRepository.getAccountOperationHistory(it) }.mapChildParallel(viewModelScope) {
        TransactionRepository.getOperationDetail(it.operation).apply { blockHeight = it.blockNum }
    }.map(viewModelScope) {
        val blocks = ChainPropertyRepository.getBlockHeaderBatchLocal(*it.map { it.blockHeight }.toLongArray())
        it.onEach { operation -> operation.createTime = blocks[operation.blockHeight]?.timestamp ?: operation.createTime }.asReversed()
    }

    fun checkAccountHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            AccountRepository.getAccountHistory(accountUid.value)
        }
    }

    fun addCurrentForObserve() {
        blockchainDatabaseScope.launch {
            account.value?.let { LocalUserRepository.addForObserve(it) }
        }
    }

    var isPicker = false

    // FIXME: 2020/10/11
    fun setAccountUid(uid: Long) {
        if (uid != ChainConfig.EMPTY_INSTANCE) {
            accountUid.value = uid
            userUid.value = uid
        }
    }

    override fun onActivityIntent(intent: Intent?) {
        super.onActivityIntent(intent)
        intent ?: return
        logcat("onActivityIntent", intent.action)
        when (intent.action) {
            Intent.ACTION_MAIN -> return
            Intent.ACTION_VIEW -> {
                val uri = intent.data?.normalizeScheme()
                if (uri != null) {
                    viewModelScope.launch {
                        val accountPath = uri.pathSegments.firstOrNull()
                        val account = resolveAccountPath(accountPath)
                        withContext(Dispatchers.Main) { setAccountUid(account.uid) }
                    }
                }
            }
            null -> {
                val accountInstance = intent.getJson(IntentParameters.Account.KEY_UID, ChainConfig.EMPTY_INSTANCE)
                val chainId = intent.getJson(IntentParameters.Chain.KEY_CHAIN_ID, ChainConfig.EMPTY_STRING_ID)
                if (chainId != ChainConfig.EMPTY_STRING_ID) {
                    chainIdManual.value = chainId
                    accountUid.value = if (chainId == ChainPropertyRepository.chainId) accountInstance else ChainConfig.EMPTY_INSTANCE
                    userUid.value = accountInstance
                } else {
                    accountUid.value = accountInstance
                    userUid.value = accountInstance
                }
            }
            else -> return
        }

    }

}