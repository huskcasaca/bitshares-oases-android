package com.bitshares.oases.ui.account_ktor.browser

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.ui.account_ktor.K_AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.raw_data.JsonRawDataViewModel
import graphene.chain.K102_AccountObject
import graphene.chain.K217_CollateralBidObject
import graphene.protocol.PriceType
import graphene.protocol.emptyIdType
import modulon.extensions.view.StringResTabs
import modulon.extensions.view.attachEnumsViewPager2
import modulon.extensions.view.nextView
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout

class K_AccountBrowserFragment : ContainerFragment() {

    private val viewModel: K_AccountViewModel by activityViewModels()
    private val rawViewModel: JsonRawDataViewModel by activityViewModels()

    enum class Tabs(override val stringRes: Int): StringResTabs {
        INFO(R.string.account_browser_tab_basic_info),
        BALANCE(R.string.account_browser_tab_balance),
        ACTIVITY(R.string.account_browser_tab_activity),
        LIMIT_ORDERS(R.string.account_browser_tab_limit_order),
        AUTHORITY(R.string.account_browser_tab_authority),
        VOTES(R.string.account_browser_tab_votes),
        WHITELIST(R.string.account_browser_tab_whitelist),
        RAW(R.string.tab_raw_data),
    }

    override fun onCreateView() {

        // TODO: 2022/2/17  distinct between fragments
//        secureWindow()
        setupAction {
            titleConnectionState(context.getString(R.string.account_browser_title))
            websocketStateMenu()
            walletStateMenu()

            val a = K217_CollateralBidObject(
                emptyIdType(),
                emptyIdType(),
                PriceType.INVALID
            )
//
//            if (a.bidder is K102_AccountObject) {
//                a.bidder.blackListedAccounts
//            }

//            menu {
//                text = context.getString(R.string.account_observe)
//                icon = R.drawable.ic_menu_remove_observer.contextDrawable()
//                contentDescription = context.getString(R.string.account_observe_description)
//                viewModel.isAccountObservable.observe(viewLifecycleOwner) {
//                    icon = if (it) R.drawable.ic_menu_add_observer.contextDrawable() else R.drawable.ic_menu_remove_observer.contextDrawable()
//                    doOnClick {
//                        if (it) {
//                            toast(getString(R.string.account_observe_tip))
//                            icon = R.drawable.ic_menu_add_observer.contextDrawable()
//                            (icon as AnimatedVectorDrawable).start()
//                            viewModel.addCurrentForObserve()
//                        } else {
//                            toast(getString(R.string.account_observe_repeat_tip))
//                        }
//                    }
//                }
//            }
//            viewModel.accountName.observe(viewLifecycleOwner) { subtitle(it.toUpperCase(Locale.ROOT)) }
//            viewModel.accountNonNull.observe(viewLifecycleOwner) {
//                rawViewModel.setContent(it)
//            }
//        }
        setupVertical {
            tabLayout {
                post { attachEnumsViewPager2<Tabs>(nextView()) }
            }
            pagerLayout {
                offscreenPageLimit = 1
                attachFragmentListAdapter(Tabs.values()) {
                    when (it) {
                        Tabs.INFO -> K_AccountBrowserFragment_Info()
//                        Tabs.BALANCE -> AccountBrowserFragment_Balance()
//                        Tabs.ACTIVITY -> AccountBrowserFragment_Activity()
//                        Tabs.LIMIT_ORDERS -> AccountBrowserFragment_LimitOrders()
//                        Tabs.AUTHORITY -> AccountBrowserFragment_Authority()
//                        Tabs.VOTES -> AccountBrowserFragment_Votes()
//                        Tabs.WHITELIST -> AccountBrowserFragment_Whitelist()
//                        Tabs.RAW -> JsonRawDataFragment()
                        else -> K_AccountBrowserFragment_Info()
                    }
                }
            }
        }
    }

//    private fun showBalanceSortDialog() = showBottomDialog {
//        fun sort(method: AccountViewModel.BalanceSortMethod) {
//            viewModel.sortBalanceBy(method)
//            dismiss()
//        }
//        title = "Sort By"
//        section {
//            cell {
//                title = "Default"
//                doOnClick { sort(AccountViewModel.BalanceSortMethod.EMPTY) }
//            }
//            cell {
//                title = "Name"
//                doOnClick { sort(AccountViewModel.BalanceSortMethod.SYMBOL) }
//            }
//            cell {
//                title = "Balance"
//                doOnClick { sort(AccountViewModel.BalanceSortMethod.BALANCE) }
//            }
//            cell {
//                title = "Type"
//                doOnClick { sort(AccountViewModel.BalanceSortMethod.TYPE) }
//            }
//        }
    }

}