package com.bitshares.oases.ui.account.whitelist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import bitshareskit.objects.AccountObject
import bitshareskit.operations.AccountWhitelistOperation
import bitshareskit.serializer.grapheneInstanceComparator
import com.bitshares.oases.netowrk.java_websocket.TransactionBuilder
import com.bitshares.oases.netowrk.java_websocket.buildTransaction
import com.bitshares.oases.ui.account.AccountViewModel
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.livedata.combineBooleanAny
import modulon.extensions.livedata.combineLatest
import modulon.extensions.livedata.filterNotNull
import java.util.*

class WhitelistViewModel(application: Application) : AccountViewModel(application) {

    val blacklistedToAppend = NonNullMutableLiveData<TreeSet<AccountObject>>(sortedSetOf(grapheneInstanceComparator))
    val blacklistedToRemove = NonNullMutableLiveData<TreeSet<AccountObject>>(sortedSetOf(grapheneInstanceComparator))

    val whitelistedToAppend = NonNullMutableLiveData<TreeSet<AccountObject>>(sortedSetOf(grapheneInstanceComparator))
    val whitelistedToRemove = NonNullMutableLiveData<TreeSet<AccountObject>>(sortedSetOf(grapheneInstanceComparator))

    private val blacklistToAppend = sortedSetOf<AccountObject>(grapheneInstanceComparator)
    private val blacklistToRemove = sortedSetOf<AccountObject>(grapheneInstanceComparator)

    private val whitelistToAppend = sortedSetOf<AccountObject>(grapheneInstanceComparator)
    private val whitelistToRemove = sortedSetOf<AccountObject>(grapheneInstanceComparator)

    val blacklistChanged =
        combineLatest(blacklisted, blacklistedToAppend, blacklistedToRemove) { blacklisted, blacklistedToAppend, blacklistedToRemove ->
            sortedSetOf(grapheneInstanceComparator,
                *(blacklisted.orEmpty() + blacklistedToAppend.orEmpty() - blacklistedToRemove.orEmpty()).toTypedArray())
        }
    val whitelistChanged =
        combineLatest(whitelisted, whitelistedToAppend, whitelistedToRemove) { whitelisted, whitelistedToAppend, whitelistedToRemove ->
            sortedSetOf(grapheneInstanceComparator,
                *(whitelisted.orEmpty() + whitelistedToAppend.orEmpty() - whitelistedToRemove.orEmpty()).toTypedArray())
        }

    val isBlacklistChanged = combineBooleanAny(blacklistedToAppend.map { it.isNotEmpty() }, blacklistedToRemove.map { it.isNotEmpty() })
    val isWhitelistChanged = combineBooleanAny(whitelistedToAppend.map { it.isNotEmpty() }, whitelistedToRemove.map { it.isNotEmpty() })

//    val isBlacklistModified = combineNonNull(blacklisted, blacklistChanged)
//    val isWhitelistModified = combineBooleanAny(whitelistedToAppend.map { it.isNotEmpty() }, whitelistedToRemove.map { it.isNotEmpty() })


    val isModified = combineBooleanAny(isBlacklistChanged, isWhitelistChanged)

    fun isModified() = isModified.value ?: false

    @Synchronized
    fun addBlacklistedAccount(obj: AccountObject) {
        when {
            blacklistToRemove.contains(obj) -> {
                blacklistToRemove.remove(obj)
                blacklistedToRemove.value = blacklistToRemove
            }
            !blacklistToAppend.contains(obj) && blacklisted.value?.contains(obj) != true -> {
                blacklistToAppend.add(obj)
                blacklistedToAppend.value = blacklistToAppend
            }
        }
    }

    @Synchronized
    fun removeBlacklistedAccount(obj: AccountObject) {
        when {
            blacklistToAppend.contains(obj) -> {
                blacklistToAppend.remove(obj)
                blacklistedToAppend.value = blacklistToAppend
            }
            !blacklistToRemove.contains(obj) && blacklisted.value?.contains(obj) == true -> {
                blacklistToRemove.add(obj)
                blacklistedToRemove.value = blacklistToRemove
            }
        }
    }

    @Synchronized
    fun addWhitelistedAccount(obj: AccountObject) {
        when {
            whitelistToRemove.contains(obj) -> {
                whitelistToRemove.remove(obj)
                whitelistedToRemove.value = whitelistToRemove
            }
            !whitelistToAppend.contains(obj) && whitelisted.value?.contains(obj) != true -> {
                whitelistToAppend.add(obj)
                whitelistedToAppend.value = whitelistToAppend
            }
        }
    }

    @Synchronized
    fun removeWhitelistedAccount(obj: AccountObject) {
        when {
            whitelistToAppend.contains(obj) -> {
                whitelistToAppend.remove(obj)
                whitelistedToAppend.value = whitelistToAppend
            }
            !whitelistToRemove.contains(obj) && whitelisted.value?.contains(obj) == true -> {
                whitelistToRemove.add(obj)
                whitelistedToRemove.value = whitelistToRemove
            }
        }
    }

    val transactionBuilder = MutableLiveData<TransactionBuilder>()
    val transaction = transactionBuilder.map { it.build() }
    val operation = transaction.map { it.operations.firstOrNull() as? AccountWhitelistOperation }.filterNotNull()

    fun buildTransaction(): TransactionBuilder = buildTransaction {
        val blacklist = blacklisted.value.orEmpty().toSortedSet(grapheneInstanceComparator)
        val whitelist = whitelisted.value.orEmpty().toSortedSet(grapheneInstanceComparator)
        val blacklistChanged = blacklistChanged.value.orEmpty().toSortedSet(grapheneInstanceComparator)
        val whitelistChanged = whitelistChanged.value.orEmpty().toSortedSet(grapheneInstanceComparator)
//            const val NO_LISTING: UByte = 0x00U // No opinion is specified about this account
//            const val WHITE_LISTED: UByte = 0x01U // This account is whitelisted, but not blacklisted
//            const val BLACK_LISTED: UByte = 0x02U // This account is blacklisted, but not whitelisted
//            const val WHITE_AND_BLACK_LISTED: UByte = 0x03U // This account is both whitelisted and blacklisted
        val no = sortedSetOf<AccountObject>(grapheneInstanceComparator)
        val white = sortedSetOf<AccountObject>(grapheneInstanceComparator)
        val black = sortedSetOf<AccountObject>(grapheneInstanceComparator)
        val both = sortedSetOf<AccountObject>(grapheneInstanceComparator)

        blacklistToRemove.forEach {
            if (whitelistChanged.contains(it)) white.add(it) else no.add(it)
        }
        blacklistToAppend.forEach {
            if (whitelistChanged.contains(it)) both.add(it) else black.add(it)
        }
        whitelistToRemove.forEach {
            if (blacklistChanged.contains(it)) black.add(it) else no.add(it)
        }
        whitelistToAppend.forEach {
            if (blacklistChanged.contains(it)) both.add(it) else white.add(it)
        }
        no.forEach {
            addOperation {
                AccountWhitelistOperation(account.value!!, it, AccountWhitelistOperation.NO_LISTING)
            }
        }
        white.forEach {
            addOperation {
                AccountWhitelistOperation(account.value!!, it, AccountWhitelistOperation.WHITE_LISTED)
            }
        }
        black.forEach {
            addOperation {
                AccountWhitelistOperation(account.value!!, it, AccountWhitelistOperation.BLACK_LISTED)
            }
        }
        both.forEach {
            addOperation {
                AccountWhitelistOperation(account.value!!, it, AccountWhitelistOperation.WHITE_AND_BLACK_LISTED)
            }
        }
        onSuccess {
            listOf(blacklistToRemove, blacklistToAppend, whitelistToRemove, whitelistToAppend).forEach { it.clear() }
            blacklistedToAppend.value = blacklistedToAppend.value.apply { clear() }
            blacklistedToRemove.value = blacklistedToRemove.value.apply { clear() }
            whitelistedToAppend.value = whitelistedToAppend.value.apply { clear() }
            whitelistedToRemove.value = whitelistedToRemove.value.apply { clear() }
        }
        transactionBuilder.value = this
        checkFees()
    }

}

