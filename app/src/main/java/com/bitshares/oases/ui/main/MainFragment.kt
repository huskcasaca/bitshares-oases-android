package com.bitshares.oases.ui.main

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.account.picker.AccountPickerViewModel
import com.bitshares.oases.ui.account.voting.VotingViewModel
import com.bitshares.oases.ui.asset.picker.AssetPickerViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startFragment
import com.bitshares.oases.ui.intro.IntroFragment
import com.bitshares.oases.ui.main.balance.BalanceFragment
import com.bitshares.oases.ui.main.dashboard.DashboardFragment
import com.bitshares.oases.ui.main.drawer.DrawerFragment
import com.bitshares.oases.ui.main.explore.ExploreFragment
import com.bitshares.oases.ui.main.explore.ExploreViewModel
import com.bitshares.oases.ui.main.market.MarketFragment
import com.bitshares.oases.ui.main.market.MarketViewModel
import com.bitshares.oases.ui.main.settings.MainSettingsFragment
import com.bitshares.oases.ui.testlab.TestLabFragment
import modulon.dialog.button
import modulon.extensions.compat.activity
import modulon.extensions.compat.recreate
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.livedata.skipFirst
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.actionbar.*
import modulon.layout.coordinator.behavior.ActionBarBehavior
import modulon.layout.coordinator.behavior.ContainerScrollingBehavior
import modulon.layout.navigation.BottomNavigationLayout
import modulon.layout.navigation.button
import kotlin.math.abs

class MainFragment : ContainerFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val marketViewModel: MarketViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()
    private val exploreViewModel: ExploreViewModel by activityViewModels()

    private val votingViewModel: VotingViewModel by activityViewModels()
    private val accountSearchingViewModel: AccountPickerViewModel by activityViewModels()
    private val assetSearchingViewModel: AssetPickerViewModel by activityViewModels()


    enum class Tabs(val stringRes: Int, val iconRes: Int) {
        DASHBOARD(R.string.bottom_dashboard, R.drawable.ic_bottom_dashboard),
        BALANCE(R.string.bottom_balance, R.drawable.ic_bottom_balance),
        MARKET(R.string.bottom_defi, R.drawable.ic_bottom_market),
//        LIQUID(R.string.bottom_liquid, R.drawable.ic_bottom_liquid),

        EXPLORE(R.string.bottom_explore, R.drawable.ic_bottom_explore),
        SETTINGS(R.string.bottom_settings, R.drawable.ic_bottom_explore),
    }
    
    private val enableTabs: Array<Tabs> = if (AppConfig.ENABLE_DRAWER) arrayOf(Tabs.DASHBOARD, Tabs.BALANCE, Tabs.MARKET, Tabs.EXPLORE) else Tabs.values()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCoordinator {
            drawerLayout {
                verticalLayout {
                    backgroundTintColor = context.getColor(R.color.background)
                    fitsSystemWindows = true
                    frameLayout {
                        coordinatorLayout {
                            fitsSystemWindows = true
                            viewRow<ActionBarLayout> {
                                actionMenu {
                                    isVisible = AppConfig.ENABLE_DRAWER
                                    translation = translation
                                    icon = R.drawable.ic_cell_drawer.contextDrawable()
                                    doOnClick { mainViewModel.openDrawer() }
                                }
                                menu {
                                    icon = R.drawable.ic_test_menu_search.contextDrawable()
                                    // TODO: 27/1/2022 inline and prevent keyboard shows up
                                    fun createActionView() {
                                        actionView = create<SearchLayout> {
                                            queryHint = "Search..."
                                            fieldtextView.parentViewGroup.isFocusableInTouchMode = false
                                            fieldtextView.doAfterTextChanged {
                                                exploreViewModel.filter.value = it.toStringOrEmpty()
                                                votingViewModel.filter.value = it.toStringOrEmpty()
                                            }
                                            mainViewModel.currentExploreTab.observe { exploreTab ->
                                                queryHint = when (exploreTab) {
                                                    ExploreFragment.Tabs.BLOCKCHAIN -> "Search Blocks..."
                                                    ExploreFragment.Tabs.WITNESS -> "Search Witness..."
                                                    ExploreFragment.Tabs.COMMITTEE -> "Search Committee..."
                                                    ExploreFragment.Tabs.WORKER -> "Search Worker..."
                                                    ExploreFragment.Tabs.ACCOUNT -> "Search Account..."
                                                    ExploreFragment.Tabs.ASSET -> "Search Asset..."
                                                    ExploreFragment.Tabs.MARKET -> "Search Market..."
                                                    ExploreFragment.Tabs.FEE_SCHEDULE -> "Search Fee Schedule..."
                                                }
                                                collapseActionView()
                                            }
                                        }
                                    }
                                    doOnClick {
                                        if (actionView == null) createActionView()
                                        expandActionView()
                                        postDelayed(500) {
                                            (actionView as SearchLayout).fieldtextView.requestFocus()
                                            (actionView as SearchLayout).fieldtextView.showSoftKeyboard()
                                        }
                                    }
                                    doOnExpand {
                                        actionMenu {
                                            icon = R.drawable.ic_cell_back_arrow.contextDrawable()
                                            doOnClick { collapseActionView() }
                                        }
                                        doOnBackPressed {
                                            collapseActionView()
                                            false
                                        }
                                    }
                                    doOnCollapse {
                                        postDelayed(500) {
                                            (actionView as SearchLayout).fieldtextView.text = null
                                        }
//                                    exploreViewModel.filter.value = EMPTY_SPACE
                                        actionMenu {
                                            icon = R.drawable.ic_cell_drawer.contextDrawable()
                                            doOnClick { mainViewModel.openDrawer() }
                                        }
                                        doOnBackPressed {
                                            activity.finish()
                                            true
                                        }
                                    }
                                    combineNonNull(mainViewModel.currentMainTab, mainViewModel.currentExploreTab).observe { (mainTab, exploreTab) ->
                                        isVisible = mainTab == Tabs.EXPLORE
                                        isExpanded = false
                                    }
                                }
                                menu {
                                    icon = R.drawable.ic_test_pause.contextDrawable()
                                    doOnClick {
                                        Settings.KEY_ENABLE_BLOCK_UPDATES.value = !Settings.KEY_ENABLE_BLOCK_UPDATES.value
                                    }
                                    Settings.KEY_ENABLE_BLOCK_UPDATES.observe {
                                        icon = if (it) R.drawable.ic_test_pause.contextDrawable() else R.drawable.ic_test_play.contextDrawable()
                                    }
                                    combineNonNull(mainViewModel.currentMainTab, mainViewModel.currentExploreTab).observe { (mainTab, exploreTab) ->
                                        isVisible = mainTab == Tabs.EXPLORE && exploreTab == ExploreFragment.Tabs.BLOCKCHAIN
                                    }
                                }
                                menu {
                                    icon = R.drawable.ic_menu_add.contextDrawable()
                                    doOnClick {
                                        exploreViewModel.showLifetimeFeeParameters.value = !exploreViewModel.showLifetimeFeeParameters.value
                                    }
                                    combineNonNull(mainViewModel.currentMainTab, mainViewModel.currentExploreTab).observe { (mainTab, exploreTab) ->
                                        isVisible = mainTab == Tabs.EXPLORE && exploreTab == ExploreFragment.Tabs.FEE_SCHEDULE
                                    }
                                }
                                networkStateMenu()
                                walletStateMenu()
                                combineNonNull(mainViewModel.currentMainTab, exploreViewModel.isUpdatesEnabled).observe(viewLifecycleOwner) { (tab, update) ->
                                    if (tab == Tabs.EXPLORE) subtitle(if (update) "Syncing..." else "Paused")
                                }
                                combineNonNull(mainViewModel.currentMainTab, accountViewModel.totalAmount).observe(viewLifecycleOwner) { (tab, amount) ->
                                    if (tab == Tabs.BALANCE) subtitle = amount.toString()
                                }
                                // TODO: 27/12/2021 distinct
                                mainViewModel.currentMainTab.observe(viewLifecycleOwner) {
                                    title = context.getString(R.string.app_name)
                                    subtitle = context.getString(it.stringRes).uppercase()
                                }
                                layoutParams = CoordinatorLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply { behavior = ActionBarBehavior() }
                            }
                            pagerLayout {
                                attachFragmentListAdapter(enableTabs) { tab ->
                                    when (tab) {
                                        Tabs.DASHBOARD -> DashboardFragment()
                                        Tabs.BALANCE -> BalanceFragment()
                                        Tabs.MARKET -> MarketFragment()
//                                    Tabs.LIQUID -> LiquidPoolFragment()
                                        Tabs.EXPLORE -> ExploreFragment()
                                        Tabs.SETTINGS -> MainSettingsFragment()
                                    }
                                }
                                isUserInputEnabled = false
                                offscreenPageLimit = 2
                                doOnPageSelected { mainViewModel.currentMainTab.value = enableTabs[it] }
                                doOnPageScrolled { position, positionOffset, _ -> mainViewModel.currentPosition.value = position + positionOffset }
                                mainViewModel.selectedMainTab.observe { setCurrentItem(it.ordinal, false) }
                                layoutParams = CoordinatorLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply { behavior = ContainerScrollingBehavior() }
                            }
                            /*
                            view<FloatingButton> {
                                isVisible = false
                                layoutParams = coordinatorParams(WRAP_CONTENT, WRAP_CONTENT) {
                                    gravity = Gravity.BOTTOM or Gravity.END
                                    rightMargin = 16.dp
                                    bottomMargin = 16.dp
                                }
                                setImageDrawable(R.drawable.ic_menu_add.contextDrawable())
                                doOnClick {
                                    showAddTradePairDialog()
                                }
                                mainViewModel.currentMainTab.observe(viewLifecycleOwner) {
                                    if (it == Tabs.MARKET) show() else hide()
                                }
                            }
                            */
                            setFrameParamsFill()
                        }
                        shader(Gravity.BOTTOM)
                        setLinearParamsRow(height = 0, weight = 1f)
                    }
                    viewRow<BottomNavigationLayout> {
                        val interceptor = AccelerateDecelerateInterpolator()
                        enableTabs.forEach { tab: Tabs ->
                            button {
                                if (tab.ordinal == 0) isChecked = true
                                text = context.getString(tab.stringRes)
                                icon = tab.iconRes.contextDrawable()
                                mainViewModel.currentMainTab.observe(viewLifecycleOwner) { isChecked = it == tab }
                                mainViewModel.currentPosition.observe(viewLifecycleOwner) {
                                    val offset = interceptor.getInterpolation(abs(it - tab.ordinal).coerceIn(0f..1f))
                                    if (progress != offset) progress = offset
                                }
                                doOnClick {
                        //                                    if (tab == Tabs.MARKET) marketViewModel.selectedMarket.value = 0
                                    mainViewModel.bottomItemClicked.value = tab
                                    mainViewModel.selectedMainTab.value = tab
                                    mainViewModel.mainTab = tab
                                }
                                doOnLongClick { }
                            }
                        }

                    }
                    setDrawerParams()
                }
                if (AppConfig.ENABLE_DRAWER) {
                    fragmentContainer {
                        fitsSystemWindows = true
                        setFragment<DrawerFragment>()
                        setDrawerParams {
                            width = context.resources.getDimensionPixelOffset(R.dimen.navigation_drawer_width)
                            gravity = Gravity.START
                        }
                        backgroundTintColor = context.getColor(R.color.background)
                    }
                    mainViewModel.isDrawerExpanded.observe(viewLifecycleOwner) {
                        if (it) openDrawer(Gravity.START) else closeDrawer(Gravity.START)
                    }
                    doOnDrawerClosed { mainViewModel.collapseList() }
                    drawerElevation = 2.dpf
                }
                setParamsFill()
            }
        }

        startFragment<TestLabFragment>()
        globalPreferenceManager.LANGUAGE.skipFirst().observe { recreate() }
        Settings.KEY_CURRENT_ACCOUNT_ID.observe { accountViewModel.setAccountUid(it) }
        if (!mainViewModel.isInitialized) {
            startFragment<IntroFragment>()
            globalPreferenceManager.IS_INITIALIZED.value = true
            globalWalletManager.reset()
        }

    }

    private fun showWelcomeDialog() = showBottomDialog {
        title = "Welcome"
        message =
            "Welcome to BitShares! BitShares Blockchain implements an industrial-grade technology focused on businesses, organizations or individuals, with an amazing eco-system and free-marketInternal economy. If you have any issues or questions using this app, please read the document provided in help page or visit official website bitshares.org."
        isCancelableByButtons = true
        button { text = "UNDERSTOOD" }
    }

}