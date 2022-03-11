package com.bitshares.oases.ui.account.margin

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.extensions.isSmartcoin
import bitshareskit.extensions.symbolOrId
import bitshareskit.objects.AssetObject
import bitshareskit.objects.CallOrder
import com.bitshares.oases.extensions.compat.startCollateral
import com.bitshares.oases.extensions.viewbinder.bindCallOrderTable
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startAssetPicker
import kotlinx.coroutines.launch
import modulon.component.ComponentCell
import modulon.component.buttonStyle
import modulon.component.isButtonEnabled
import modulon.dialog.buttonCancel
import modulon.dialog.dismissWith
import modulon.dialog.section
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.compat.secureWindow
import modulon.extensions.compat.showSuspendedBottomDialog
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.verticalLayout
import modulon.layout.actionbar.subtitle
import modulon.layout.actionbar.title
import modulon.layout.recycler.*
import modulon.union.Union
import java.util.*

class MarginPositionFragment : ContainerFragment() {

    private val viewModel: MarginPositionViewModel by activityViewModels()

    override fun onViewCreated(savedInstanceState: Bundle?) {
        secureWindow()
        setupAction {
            title("Margin Positions")
            networkStateMenu()
            walletStateMenu()
            viewModel.accountName.observe(viewLifecycleOwner) { subtitle(it.toUpperCase(Locale.ROOT)) }
        }
        setupRecycler {
            section {
                header = "Margin Positions"
                list<ComponentCell, CallOrder> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindCallOrderTable(it)
                        doOnClick { startCollateral(it.borrower.uid, it.debt.asset.uid) }
                    }
                    distinctContentBy { }
                    viewModel.callOrdersExtended.observe(viewLifecycleOwner, adapter::submitList)
                }
                isVisible = false
                viewModel.callOrders.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            section {
                cell {
                    buttonStyle()
                    title = "Borrow Asset"
                    isButtonEnabled = false
                    viewModel.accountNonNull.observe {
                        isButtonEnabled = true
                        doOnClick {
                            lifecycleScope.launch {
                                startCollateral(it.uid, showBorrowNewAssetDialog().uid)
                            }
                        }
                    }
                }
            }
        }
    }

}

suspend fun Union.showBorrowNewAssetDialog() = showSuspendedBottomDialog<AssetObject> {
    val viewModel: MarginPositionViewModel by activityViewModels()
    title = "Borrow Asset"
    message = "Loading..."
    section {
        verticalLayout {
            viewModel.priceUnits.observe(viewLifecycleOwner) {
                message = EMPTY_SPACE
                removeAllViews()
                it.forEach {
                    if (it.isSmartcoin()) {
                        cell {
                            title = it.symbolOrId
                            doOnClick {
                                dismissWith(it)
                            }
                        }
                    }
                }
            }
        }
        cell {
            title = "Other Asset"
            doOnClick {
                startAssetPicker {
                    // FIXME: 2022/2/18
                    if (it != null) dismissWith(it) else dismiss()
                }
            }
        }
    }
    buttonCancel()
}
