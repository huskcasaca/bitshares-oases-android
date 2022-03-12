package com.bitshares.oases.ui.main

import android.app.Application
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.ui.base.BaseViewModel
import com.bitshares.oases.ui.main.balance.BalanceFragment
import com.bitshares.oases.ui.main.explore.ExploreFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.livedata.filterNotNull
import modulon.extensions.livedata.invert

class MainViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private val DEFAULT_PAGE = MainFragment.Tabs.DASHBOARD
        private val DEFAULT_BALANCE_PAGE = BalanceFragment.Tabs.BALANCES
        private val DEFAULT_EXPLORE_PAGE = ExploreFragment.Tabs.BLOCKCHAIN
    }

    val isDrawerExpanded = NonNullMutableLiveData(false)

    val isUsersExpanded = NonNullMutableLiveData(false)

    val users = LocalUserRepository.decryptedList(globalWalletManager)
    val userCurrent = LocalUserRepository.decryptCurrentUserOnly(globalWalletManager)
    val userAccount = LocalUserRepository.currentUserAccount


    val usersFiltered = combineNonNull(users, userCurrent) { users, userCurrent ->
        users - userCurrent
    }

    val isUserAvailable = userCurrent.map { it != null }.distinctUntilChanged()

    val currentMainTab = NonNullMutableLiveData(DEFAULT_PAGE)
    val currentBalanceTab = NonNullMutableLiveData(DEFAULT_BALANCE_PAGE)
    val currentExploreTab = NonNullMutableLiveData(DEFAULT_EXPLORE_PAGE)


    val selectedMainTab = NonNullMutableLiveData(DEFAULT_PAGE)
    val selectedBalanceTab = NonNullMutableLiveData(DEFAULT_BALANCE_PAGE)
    val selectedExploreTab = NonNullMutableLiveData(DEFAULT_EXPLORE_PAGE)

    fun select(tab1: MainFragment.Tabs, tab2: Enum<*>) {
        selectedMainTab.value = tab1
        when (tab2) {
            is BalanceFragment.Tabs -> selectedBalanceTab.value = tab2
        }
    }

    val currentPosition = NonNullMutableLiveData(0f)
    val bottomItemClicked = NonNullMutableLiveData(MainFragment.Tabs.DASHBOARD)
    val doubleClickedPage = combineNonNull(currentMainTab, bottomItemClicked) { current, clicked -> if (current == clicked) current else null }.filterNotNull()

    val isMarketSelected = combineNonNull(currentMainTab, isUserAvailable) { tab, user ->
        tab == MainFragment.Tabs.MARKET && user
    }

    val isExploreSelected = combineNonNull(currentMainTab, isUserAvailable) { tab, user ->
        tab == MainFragment.Tabs.EXPLORE
    }

    var mainTab: MainFragment.Tabs
        get() = currentMainTab.value
        set(tab) {
            if (tab != currentMainTab.value) {
                currentMainTab.value = tab
            }
        }

    var balanceTab: BalanceFragment.Tabs
        get() = currentBalanceTab.value
        set(tab) {
            if (tab != currentBalanceTab.value) {
                currentBalanceTab.value = tab
            }
        }

    var exploreTab: ExploreFragment.Tabs
        get() = currentExploreTab.value
        set(tab) {
            if (tab != currentExploreTab.value) {
                currentExploreTab.value = tab
            }
        }

    fun closeDrawer() {
        viewModelScope.launch {
            delay(300L)
            isUsersExpanded.value = false
            isDrawerExpanded.value = false
        }
    }

    fun openDrawer() {
        isDrawerExpanded.value = true
    }

    fun changeExpandState() {
        isUsersExpanded.value = !isUsersExpanded.value
    }

    fun collapseList() {
        isUsersExpanded.value = false
    }

    val isInitialized: Boolean
        get() = (globalPreferenceManager.IS_INITIALIZED.value)
//            .also {
//            if (!it) MainApplication.WALLET.initialize()
//            Settings.KEY_IS_INITIALIZED.value = true
//        }

    val isBalancesExpanded = NonNullMutableLiveData(false)

    val isLimitOrdersExpanded = NonNullMutableLiveData(false)

    val isMarginPositionsExpanded = NonNullMutableLiveData(false)


    fun invertBalance() {
        isBalancesExpanded.invert()
    }
    fun invertLimitOrder() {
        isLimitOrdersExpanded.invert()
    }
    fun invertMargin() {
        isMarginPositionsExpanded.invert()
    }


}