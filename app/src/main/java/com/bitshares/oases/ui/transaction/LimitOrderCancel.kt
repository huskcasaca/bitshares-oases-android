package com.bitshares.oases.ui.transaction

import android.app.Application
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import bitshareskit.models.Market
import bitshareskit.operations.LimitOrderCancelOperation
import com.bitshares.oases.R
import com.bitshares.oases.extensions.text.createAccountSpan
import com.bitshares.oases.extensions.viewbinder.bindLimitOrderTable
import com.bitshares.oases.extensions.viewbinder.feeCell
import com.bitshares.oases.provider.chain_repo.MarketRepository
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.livedata.filterNotNull
import modulon.extensions.livedata.map
import modulon.extensions.view.updatePaddingVerticalV6
import modulon.extensions.viewbinder.cell
import modulon.union.Union

// TODO: 24/10/2021 extract all transaction builders from viewModel and make it open to isolate all transaction broadcast dialog
fun Union.showLimitOrderCancelDialog(operation: LimitOrderCancelOperation, market: Market) = showBottomDialog {
    val viewModel: LimitOrderCancelViewModel by viewModels()
    section {
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.operation_limit_order_cancel_seller)
            viewModel.operationCreator.observe(viewLifecycleOwner) { subtitle = createAccountSpan(it) }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.operation_limit_order_cancel_order_to_cancel)
        }
        cell {
            updatePaddingVerticalV6()
            viewModel.limitOrder.observe(viewLifecycleOwner) { bindLimitOrderTable(it) }
        }
        feeCell(union, viewModel.transactionBuilder)
    }
    bindTransaction(viewModel.buildTransaction(operation), viewModel)
    viewModel.setMarket(market)
}


class LimitOrderCancelViewModel(application: Application) : TransactionBroadcastViewModel(application) {

    val operation = operations.map { it.firstOrNull() as? LimitOrderCancelOperation }.filterNotNull()
    val market = MutableLiveData<Market>()

    val limitOrder = combineNonNull(operation, market).map(viewModelScope) { (operation, market) -> MarketRepository.getOrderDetail(operation.order, market) }

    fun setMarket(market: Market) {
        this.market.value = market
    }

}