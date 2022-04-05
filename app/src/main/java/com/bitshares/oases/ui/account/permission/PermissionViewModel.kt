package com.bitshares.oases.ui.account.permission

import android.app.Application
import androidx.lifecycle.*
import bitshareskit.extensions.isNotNullOrEmpty
import bitshareskit.extensions.orFalse
import bitshareskit.models.BrainKey
import bitshareskit.models.PrivateKey
import bitshareskit.models.PublicKey
import bitshareskit.objects.AccountObject
import bitshareskit.operations.AccountUpdateOperation
import bitshareskit.serializer.grapheneInstanceComparator
import bitshareskit.serializer.publicKeyComparator
import com.bitshares.oases.chain.BrainKeyDict
import com.bitshares.oases.chain.KeyCreator
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.netowrk.java_websocket.TransactionBuilder
import com.bitshares.oases.netowrk.java_websocket.buildTransaction
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.ui.account.AuthorityViewModel
import bitshareskit.chain.Authority
import kotlinx.coroutines.launch
import modulon.extensions.charset.BLANK_SPACE
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.coroutine.debounce
import modulon.extensions.livedata.*
import java.util.*
import bitshareskit.models.Authority as AccountAuthority

open class PermissionViewModel(application: Application) : AuthorityViewModel(application) {

    val newPasswordField = MutableLiveData("")

    fun randomPassword() {
        newPasswordField.value = PrivateKey.randomPassword(ChainPropertyRepository.chainSymbol)
    }

    protected val ownerKeyAuthsChanged: MutableLiveData<SortedMap<PublicKey, UShort>> = ownerKeyAuths.map { it.toSortedMap(publicKeyComparator) }.toMutableLiveData()
    protected val activeKeyAuthsChanged: MutableLiveData<SortedMap<PublicKey, UShort>> = activeKeyAuths.map { it.toSortedMap(publicKeyComparator) }.toMutableLiveData()
    protected val memoKeyAuthsChanged: MutableLiveData<SortedMap<PublicKey, UShort>> = memoKeyAuths.map { it.toSortedMap(publicKeyComparator) }.toMutableLiveData()
    val ownerAccountAuthsChanged: MutableLiveData<SortedMap<AccountObject, UShort>> = ownerAccountAuths.map { it.toSortedMap(grapheneInstanceComparator) }.toMutableLiveData()
    val activeAccountAuthsChanged: MutableLiveData<SortedMap<AccountObject, UShort>> = activeAccountAuths.map { it.toSortedMap(grapheneInstanceComparator) }.toMutableLiveData()



    val ownerKeyAuthsChecked: LiveData<List<Triple<PublicKey, UShort, Boolean>>> = combineLatest(ownerKeyAuthsChanged, ownerKeyAuthsLocal) { changed, local ->
        changed.orEmpty().map { Triple(it.key, it.value, local.orEmpty().map { it.publicKey }.contains(it.key) || ownerKeyAuthsToAppend.map { it.key.publicKey }.contains(it.key)) }
    }.distinctUntilChanged()
    val activeKeyAuthsChecked: LiveData<List<Triple<PublicKey, UShort, Boolean>>> = combineLatest(activeKeyAuthsChanged, activeKeyAuthsLocal) { changed, local ->
        changed.orEmpty().map { Triple(it.key, it.value, local.orEmpty().map { it.publicKey }.contains(it.key) || activeKeyAuthsToAppend.map { it.key.publicKey }.contains(it.key)) }
    }.distinctUntilChanged()
    val memoKeyAuthsChecked: LiveData<List<Triple<PublicKey, UShort, Boolean>>> = combineLatest(memoKeyAuthsChanged, memoKeyAuthsLocal) { changed, local ->
        changed.orEmpty().map { Triple(it.key, it.value, local.orEmpty().map { it.publicKey }.contains(it.key) || memoKeyAuthsToAppend.map { it.key.publicKey }.contains(it.key)) }
    }.distinctUntilChanged()

    private val ownerKeyAuthsToAppend = mutableMapOf<PrivateKey, UShort>()
    private val activeKeyAuthsToAppend = mutableMapOf<PrivateKey, UShort>()
    private val memoKeyAuthsToAppend = mutableMapOf<PrivateKey, UShort>()

    private val ownerThresholdInternal = MutableLiveData<UInt?>()
    private val activeThresholdInternal = MutableLiveData<UInt?>()

    val ownerThresholdChanged = combineFirst(ownerThreshold, ownerThresholdInternal).distinctUntilChanged()
    val activeThresholdChanged = combineFirst(activeThreshold, activeThresholdInternal).distinctUntilChanged()
    val memoThresholdChanged = combineFirst(memoThreshold, emptyLiveData<UInt>()).distinctUntilChanged()

    // FIXME: 28/07/2021 threshold!! in 1U..UInt.MAX_VALUE -> (1U..UInt.MAX_VALUE).contains(threshold)
    val isOwnerSufficient = combineLatest(ownerThresholdChanged.map { it.second ?: it.first }, ownerKeyAuthsChanged, ownerAccountAuthsChanged) { threshold, keys, accounts ->
        val current = keys?.values.orEmpty().sum() + accounts?.values.orEmpty().sum()
        threshold != null && (1U..UInt.MAX_VALUE).contains(threshold) && threshold <= current
    }

    val isActiveSufficient = combineLatest(activeThresholdChanged.map { it.second ?: it.first }, activeKeyAuthsChanged, activeAccountAuthsChanged) { threshold, keys, accounts ->
        val current: UInt = keys?.values.orEmpty().sum() + accounts?.values.orEmpty().sum()
        threshold != null && (1U..UInt.MAX_VALUE).contains(threshold) && threshold <= current
    }

    val isMemoSufficient = combineLatest(memoThresholdChanged.map { it.second ?: it.first }, memoKeyAuthsChanged) { threshold, keys ->
        val current: UInt = keys?.values.orEmpty().sum()
        threshold != null && (1U..UInt.MAX_VALUE).contains(threshold) && threshold <= current
    }

    fun changeThreshold(threshold: UInt?, authority: Authority) {
        when (authority) {
            Authority.OWNER -> ownerThresholdInternal.value = if (ownerThreshold.value == threshold) null else threshold
            Authority.ACTIVE -> activeThresholdInternal.value = if (activeThreshold.value == threshold) null else threshold
            Authority.MEMO -> {
            }
        }
    }

    fun addKeyAuths(key: PrivateKey, threshold: UShort, authority: Authority) {
        if (!key.isValid) return
        when (authority) {
            Authority.OWNER -> ownerKeyAuthsToAppend[key] = threshold
            Authority.ACTIVE -> activeKeyAuthsToAppend[key] = threshold
            Authority.MEMO -> memoKeyAuthsToAppend[key] = threshold
        }
        addKeyAuths(key.publicKey, threshold, authority)
    }

    fun addKeyAuths(key: PublicKey, threshold: UShort, authority: Authority) {
        if (!key.isValid) return
        when (authority) {
            Authority.OWNER -> ownerKeyAuthsChanged.value = ownerKeyAuthsChanged.value?.apply { set(key, threshold) }
            Authority.ACTIVE -> activeKeyAuthsChanged.value = activeKeyAuthsChanged.value?.apply { set(key, threshold) }
            Authority.MEMO -> memoKeyAuthsChanged.value = sortedMapOf(publicKeyComparator, key to 1U.toUShort())
        }
    }

    fun removeKeyAuths(key: PublicKey, authority: Authority) {
        if (!key.isValid) return
        when (authority) {
            Authority.OWNER -> ownerKeyAuthsChanged.value = ownerKeyAuthsChanged.value?.apply { remove(key) }
            Authority.ACTIVE -> activeKeyAuthsChanged.value = activeKeyAuthsChanged.value?.apply { remove(key) }
            Authority.MEMO -> memoKeyAuthsChanged.value = memoKeyAuthsChanged.value?.apply { remove(key) }
        }
    }


    fun addAccountAuths(account: AccountObject, authority: Authority) {
        val exist = when (authority) {
            Authority.OWNER -> ownerAccountAuthsChanged.value
            Authority.ACTIVE -> activeAccountAuthsChanged.value
            Authority.MEMO -> emptyMap()
        }.orEmpty().containsKey(account)
        if (!exist) addAccountAuths(account, 1U, authority)
    }

    fun addAccountAuths(account: AccountObject, threshold: UShort, authority: Authority) {
        if (!account.isExist || account.uid == user.value?.uid) return
        when (authority) {
            Authority.OWNER -> ownerAccountAuthsChanged.value = ownerAccountAuthsChanged.value?.apply { set(account, threshold) }
            Authority.ACTIVE -> activeAccountAuthsChanged.value = activeAccountAuthsChanged.value?.apply { set(account, threshold) }
            Authority.MEMO -> Unit
        }
    }

    fun removeAccountAuths(account: AccountObject, authority: Authority) {
        if (!account.isExist) return
        when (authority) {
            Authority.OWNER -> ownerAccountAuthsChanged.value = ownerAccountAuthsChanged.value?.apply { remove(account) }
            Authority.ACTIVE -> activeAccountAuthsChanged.value = activeAccountAuthsChanged.value?.apply { remove(account) }
            Authority.MEMO -> Unit
        }
    }


    val ownerGenerated = NonNullMediatorLiveData<Set<PrivateKey>>(emptySet())
    val activeGenerated = NonNullMediatorLiveData<Set<PrivateKey>>(emptySet())
    val memoGenerated = NonNullMediatorLiveData<Set<PrivateKey>>(emptySet())

    val keysGenerated = combineNonNull(ownerGenerated, activeGenerated, memoGenerated) { a, b, c -> a + b + c }

    val isOwnerKey = combineNonNull(ownerKeyAuthsChanged.map { it.keys }, ownerGenerated).map { (owner, generated) ->
        generated.any { owner.contains(it.publicKey) }
    }

    val isActiveKey = combineNonNull(activeKeyAuthsChanged.map { it.keys }, activeGenerated).map { (active, generated) ->
        generated.any { active.contains(it.publicKey) }
    }

    val isMemoKey = combineNonNull(memoKeyAuthsChanged.map { it.keys }, memoGenerated).map { (active, generated) ->
        generated.any { active.contains(it.publicKey) }
    }

    private val generateKeyFromSeedInternal = debounce(viewModelScope, 240) { seed: String ->
        val name = account.value?.name
        if (!name.isNullOrBlank() && seed.isNotBlank()) {
            ownerGenerated.value = setOf(KeyCreator.createOwnerFromSeed(name, seed, ChainPropertyRepository.chainSymbol))
            activeGenerated.value = setOf(KeyCreator.createActiveFromSeed(name, seed, ChainPropertyRepository.chainSymbol))
            memoGenerated.value = setOf(KeyCreator.createMemoFromSeed(name, seed, ChainPropertyRepository.chainSymbol))
        } else {
            ownerGenerated.value = setOf()
            activeGenerated.value = setOf()
            memoGenerated.value = setOf()
        }
    }

    fun generateKeyFromSeed(seed: String) {
//        keysGenerated.value = emptySet()
        generateKeyFromSeedInternal.invoke(seed)
    }

    fun removeOwnerKeyAuths() = ownerGenerated.value.forEach { removeKeyAuths(it.publicKey, Authority.OWNER) }
    fun removeActiveKeyAuths() = activeGenerated.value.forEach { removeKeyAuths(it.publicKey, Authority.ACTIVE) }
    fun removeMemoKeyAuths() = memoGenerated.value.forEach { removeKeyAuths(it.publicKey, Authority.MEMO) }

    fun addOwnerKeyAuths() = ownerGenerated.value.forEach { addKeyAuths(it, 1U, Authority.OWNER) }
    fun addActiveKeyAuths() = activeGenerated.value.forEach { addKeyAuths(it, 1U, Authority.ACTIVE) }
    fun addMemoKeyAuths() = memoGenerated.value.forEach { addKeyAuths(it, 1U, Authority.MEMO) }


    val dialogKeyGenerated = NonNullMediatorLiveData<Set<PrivateKey>>(emptySet())

    private val generatedDialogKeyFromSeedInternal = debounce<Pair<String, Authority>>(viewModelScope) { (seed, authority) ->
        val name = account.value?.name
        dialogKeyGenerated.value = if (!name.isNullOrBlank() && seed.isNotBlank()) {
            when (authority) {
                Authority.OWNER -> setOf(KeyCreator.createOwnerFromSeed(name, seed))
                Authority.ACTIVE -> setOf(KeyCreator.createActiveFromSeed(name, seed))
                Authority.MEMO -> setOf(KeyCreator.createMemoFromSeed(name, seed))
            }
        } else emptySet()
    }

    private val generatedDialogKeyFromWifInternal = debounce(viewModelScope) { wif: String ->
        val name = account.value?.name
        dialogKeyGenerated.value = if (!name.isNullOrBlank() && wif.isNotBlank()) {
            PrivateKey.fromWif(wif, ChainPropertyRepository.chainSymbol).let {
                if (it.isValid) setOf(it) else emptySet()
            }
        } else emptySet()
    }

    private val generatedDialogKeyFromMnemonicInternal = debounce(viewModelScope) { brain: String ->
        val name = account.value?.name
        dialogKeyGenerated.value = if (!name.isNullOrBlank() && brain.isNotBlank()) {
            KeyCreator.createFromMnemonicLowercase(brain)
        } else emptySet()
    }

    // Dialog UI
    val isFieldError = NonNullMediatorLiveData(false)
    val isThresholdError = NonNullMediatorLiveData(false)

    val randomField = NonNullMutableLiveData(EMPTY_SPACE)

    fun generateRandom(type: PermissionFragment_Tabs.ImportKeyType) {
        randomField.value = when (type) {
            PermissionFragment_Tabs.ImportKeyType.CLOUD -> "P${PrivateKey.random(ChainPropertyRepository.chainSymbol).wif}"
            PermissionFragment_Tabs.ImportKeyType.WIF -> PrivateKey.random(ChainPropertyRepository.chainSymbol).wif
            PermissionFragment_Tabs.ImportKeyType.BRAIN -> BrainKey.suggest(BrainKeyDict.getDictionary(), ChainPropertyRepository.chainSymbol).words.joinToString(BLANK_SPACE)
        }
    }

    private var thresholdLocal: UShort = 0U

    fun generateDialogKeyFromSeed(seed: String, authority: Authority) {
        isFieldError.value = false
        dialogKeyGenerated.value = emptySet()
        generatedDialogKeyFromSeedInternal.invoke(seed to authority)
    }

    fun generateDialogKeyFromWif(wif: String) {
        isFieldError.value = false
        dialogKeyGenerated.value = emptySet()
        generatedDialogKeyFromWifInternal.invoke(wif)
    }

    fun generateDialogKeyFromMnemonic(mnemonic: String) {
        isFieldError.value = false
        dialogKeyGenerated.value = emptySet()
        generatedDialogKeyFromMnemonicInternal.invoke(mnemonic)
    }

    fun changeThreshold(threshold: String) {
        threshold.toUShortOrNull().also {
            isThresholdError.value = threshold.isNotBlank() && (it == null || it <= 0U)
            thresholdLocal = if (it == null || it <= 0U) 0U else it
        }
    }

    fun addDialogKeyAuths(authority: Authority): Boolean {
        return dialogKeyGenerated.value.run {
            val threshold = thresholdLocal
            if (threshold > 0U) forEach { addKeyAuths(it, threshold, authority) }
            isFieldError.value = isNullOrEmpty()
            isThresholdError.value = threshold <= 0U
            isNotEmpty() && threshold > 0U
        }
    }

    fun clearDialogKey() {
        isFieldError.value = false
        isThresholdError.value = false
        dialogKeyGenerated.value = emptySet()
        thresholdLocal = 0U
        randomField.value = EMPTY_SPACE
    }

    val transactionBuilder = MutableLiveData<TransactionBuilder>()
    val transaction = transactionBuilder.map { it.build() }
    val operation = transaction.map { it.operations.firstOrNull() as AccountUpdateOperation? }.filterNotNull()

    fun buildTransaction(): TransactionBuilder = buildTransaction {
        addOperation {
            val account = account.value!!
            val owner = if (isOwnerModified()) AccountAuthority(
                ownerThresholdChanged.value!!.let { (old, new) -> new ?: old }!!,
                ownerAccountAuthsChanged.value!!,
                ownerKeyAuthsChanged.value!!
            ) else null
            val active = if (isActiveModified()) AccountAuthority(
                activeThresholdChanged.value!!.let { (old, new) -> new ?: old }!!,
                activeAccountAuthsChanged.value!!,
                activeKeyAuthsChanged.value!!
            ) else null
            val memo = if (isMemoModified()) account.options.copy(
                memoKeyAuthsChanged.value!!.keys.first()
            ) else null
            AccountUpdateOperation(account, owner, active, memo)
        }
        onSuccess { importLocalKeys() }
        transactionBuilder.value = this
        checkFees()
    }


    private fun isOwnerModified(): Boolean {
        val acc = account.value
        val th = ownerThresholdChanged.value?.let { (old, new) -> new ?: old }
        val aa = ownerAccountAuthsChanged.value?.map { it.key.id to it.value }
        val ka = ownerKeyAuthsChanged.value
        return acc != null && ((th != null && acc.ownerMinThreshold != th) || (aa != null && acc.ownerAccountAuths.map { it.key.id to it.value } != aa) || (ka != null && acc.ownerKeyAuths != ka))
    }

    private fun isActiveModified(): Boolean {
        val acc = account.value
        val th = activeThresholdChanged.value?.let { (old, new) -> new ?: old }
        val aa = activeAccountAuthsChanged.value?.map { it.key.id to it.value }
        val ka = activeKeyAuthsChanged.value
        return acc != null && ((th != null && acc.activeMinThreshold != th) || (aa != null && acc.activeAccountAuths.map { it.key.id to it.value } != aa) || (ka != null && acc.activeKeyAuths != ka))
    }

    private fun isMemoModified(): Boolean {
        val acc = account.value
        val me = memoKeyAuthsChanged.value?.keys
        return acc?.options != null && me != null && (me.isNotEmpty() && me.first() != acc.options?.memoKey)
    }

    val isOwnerModified = combineLatest(
        account,
        ownerThresholdChanged.map { (old, new) -> new ?: old },
        ownerAccountAuthsChanged.map { it.map { it.key.id to it.value } },
        ownerKeyAuthsChanged,
    ) { acc, th, aa, ka ->
        acc != null && ((th != null && acc.ownerMinThreshold != th) || (aa != null && acc.ownerAccountAuths.map { it.key.id to it.value } != aa) || (ka != null && acc.ownerKeyAuths != ka))
    }

    val isActiveModified = combineLatest(
        account,
        activeThresholdChanged.map { (old, new) -> new ?: old },
        activeAccountAuthsChanged.map { it.map { it.key.id to it.value } },
        activeKeyAuthsChanged,
    ) { acc, th, aa, ka ->
        acc != null && ((th != null && acc.activeMinThreshold != th) || (aa != null && acc.activeAccountAuths.map { it.key.id to it.value } != aa) || (ka != null && acc.activeKeyAuths != ka))
    }

    val isMemoModified = combineLatest(
        account,
        memoKeyAuthsChanged.map { it.keys },
    ) { acc, me ->
        acc != null && me != null && (me.isNotEmpty() && me.first() != acc.options.memoKey)
    }

    //    val isModified = NonNullMediatorLiveData(false)
    val isModified = combineBooleanAny(isOwnerModified, isActiveModified, isMemoModified)

    fun isModified() = isModified.value.orFalse()

    fun checkSufficient() = isOwnerSufficient.value == true && isActiveSufficient.value == true && isMemoSufficient.value == true

    fun importLocalKeys() {
        val user = user.value
        if (user != null) {
            val owner = ownerKeyAuthsToAppend.keys.toSet()
            val active = activeKeyAuthsToAppend.keys.toSet()
            val memo = memoKeyAuthsToAppend.keys.toSet()
            blockchainDatabaseScope.launch {
                LocalUserRepository.add(
                    globalWalletManager,
                    user.copy(
                        ownerKeys = owner,
                        activeKeys = active,
                        memoKeys = memo
                    )
                )
            }
            ownerKeyAuthsToAppend.clear()
            activeKeyAuthsToAppend.clear()
            memoKeyAuthsToAppend.clear()
        }
    }

    fun importDialogKeys(vararg authority: Authority): Boolean {
        val user = user.value
        val generated = dialogKeyGenerated.value.isNotNullOrEmpty()
        if (generated && user != null) {
            blockchainDatabaseScope.launch {
                LocalUserRepository.add(
                    globalWalletManager,
                    user.copy(
                        ownerKeys = if (authority.contains(Authority.OWNER)) dialogKeyGenerated.value else emptySet(),
                        activeKeys = if (authority.contains(Authority.ACTIVE)) dialogKeyGenerated.value else emptySet(),
                        memoKeys = if (authority.contains(Authority.MEMO)) dialogKeyGenerated.value else emptySet(),
                    )
                )
            }
        } else {
            isFieldError.value = true
        }
        return generated && user != null
    }

    // threshold dialog

    val isThresholdFieldError = NonNullMutableLiveData(false)
    var thresholdField = EMPTY_SPACE
        set(value) {
            field = value
            isThresholdFieldError.value = false
        }

    fun changeThreshold(authority: Authority, component: Any?): Boolean {
        when (component) {
            // FIXME: 28/07/2021 threshold!! in 1U..toUShort.MAX_VALUE -> (1U..toUShort.MAX_VALUE).contains(threshold)
            is PublicKey, is AccountObject -> {
                val threshold = thresholdField.toUShortOrNull()
                if (threshold != null && (1U.toUShort()..UShort.MAX_VALUE).contains(threshold)) {
                    when (component) {
                        is PublicKey -> addKeyAuths(component, threshold, authority)
                        is AccountObject -> addAccountAuths(component, threshold, authority)
                    }
                    return true
                } else {
                    isThresholdFieldError.value = true
                    return false
                }
            }
            else -> {
                val threshold = thresholdField.toUIntOrNull()
                if (threshold != null && (1U..UInt.MAX_VALUE).contains(threshold)) {
                    changeThreshold(threshold, authority)
                    return true
                } else {
                    isThresholdFieldError.value = true
                    return false
                }
            }
        }
    }

}
