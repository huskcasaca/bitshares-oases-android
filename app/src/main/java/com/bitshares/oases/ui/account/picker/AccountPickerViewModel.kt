package com.bitshares.oases.ui.account.picker

import android.app.Application
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import bitshareskit.extensions.createAccountObject
import bitshareskit.objects.AccountObject
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.coroutine.debounce
import modulon.extensions.livedata.*

class AccountPickerViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val STATE_HISTORY_SHOWN = 0x00
        const val STATE_LOOKING_UP = 0x01
        const val STATE_FINISH = 0x02
        const val STATE_NOT_FOUND = 0x03
        const val STATE_NO_CONNECTION = 0x04
    }

    val searchState = NonNullMutableLiveData(STATE_HISTORY_SHOWN)

    val searchResult = NonNullMutableLiveData<List<AccountObject>>(emptyList())

    private var lastLookupName = ""

    fun lookup(lowerBoundName: String) {
        searchResult.value = emptyList()
        lastLookupName = lowerBoundName
        if (lowerBoundName.isBlank()) searchState.value = STATE_HISTORY_SHOWN else {
            searchState.value = STATE_LOOKING_UP
            lookupAccountsThrottle.invoke(lowerBoundName)
        }
    }

    val historyAccounts = Settings.KEY_ACCOUNT_SEARCH_HISTORY.distinctUntilChanged().mapChildParallel(viewModelScope) { AccountRepository.getAccountObject(it.uid) ?: it }.map { it.reversed() }
    val localAccounts = ChainPropertyRepository.currentChainId.switchMap { LocalUserRepository.getListLive(it) }.distinctChildUntilChangedBy { it.uid }
    val whitelistAccounts = Settings.KEY_CURRENT_ACCOUNT_ID.switchMap { AccountRepository.getAccountLive(it) }.map { it?.whiteListedAccount.orEmpty() }.distinctUntilChanged().mapChildParallel(viewModelScope) { AccountRepository.getAccountObject(it.uid) ?: it }

//    val availableTabs = NonNullMutableLiveData(listOf<AccountPickerFragment.Tabs>())

    val availableTabs = combineFirst(historyAccounts, localAccounts, whitelistAccounts) { history, local, whitelist ->
        listOf(
            AccountPickerFragment.Tabs.HISTORY.takeIf { history != null && history.isNotEmpty() },
            AccountPickerFragment.Tabs.LOCAL.takeIf { local != null && local.isNotEmpty() },
            AccountPickerFragment.Tabs.WHITELIST.takeIf { whitelist != null && whitelist.isNotEmpty() },
        ).filterNotNull()
    }.distinctUntilChanged().throttleLatest(viewModelScope, 64).withDefault { emptyList() }

    private val lookupAccountsThrottle = debounce<String>(viewModelScope) {
        val lookupName = it
        val num = it.length * it.length * 20
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { AccountRepository.lookupAccounts(it, num) }.onSuccess {
                if (lastLookupName == lookupName && lastLookupName.isNotBlank()) {
                    withContext(Dispatchers.Main) {
                        searchResult.value = it.map { createAccountObject(it.second, it.first) }
                        searchState.value = if (it.isEmpty()) STATE_NOT_FOUND else STATE_FINISH
                    }
                }
            }.onFailure {
                if (lastLookupName == lookupName && lastLookupName.isNotBlank()) {
                    withContext(Dispatchers.Main) {
                        searchResult.value = emptyList()
                        searchState.value = STATE_NO_CONNECTION
                    }
                }
            }
        }
    }

    fun addSearchHistory(account: AccountObject) {
        Settings.KEY_ACCOUNT_SEARCH_HISTORY.value = Settings.KEY_ACCOUNT_SEARCH_HISTORY.value + account
    }

    fun clearSearchHistory() = Settings.KEY_ACCOUNT_SEARCH_HISTORY.reset()


}