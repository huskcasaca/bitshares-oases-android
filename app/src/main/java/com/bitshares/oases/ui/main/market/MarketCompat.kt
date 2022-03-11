package com.bitshares.oases.ui.main.market

import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import bitshareskit.extensions.marketNameOrId
import bitshareskit.models.Ticker
import com.bitshares.oases.R
import com.bitshares.oases.chain.assetSymbolFilter
import com.bitshares.oases.extensions.compat.startAssetBrowser
import com.bitshares.oases.extensions.text.createAssetName
import com.bitshares.oases.extensions.viewbinder.bindMarketGroup
import com.bitshares.oases.extensions.viewbinder.bindTicker
import com.bitshares.oases.extensions.viewbinder.setTickerStyle
import modulon.component.ComponentCell
import modulon.dialog.button
import modulon.dialog.buttonCancel
import modulon.dialog.doOnDismiss
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.lifecycle.parentViewModels
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.horizontalLayout
import modulon.union.Union

fun Union.showTradePairDialog(ticker: Ticker) = showBottomDialog {
    val viewModel: MarketViewModel by parentViewModels()
//    title = context.getString(R.string.market_market_dialog)
    title = "Trade Pair"
    subtitle = ticker.market.marketNameOrId
    section {
        cell {
            // TODO: 3/9/2021 double confirm?
//            text = context.getString(R.string.market_option_remove_market)
            text = "Remove Trade Pair"
            icon = R.drawable.ic_cell_about.contextDrawable()
            doOnClick {
                showRemoveTradePairDialog(ticker)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.market_option_browse_asset)
            icon = R.drawable.ic_cell_about.contextDrawable()
            subtext = createAssetName(ticker.market.quote)
            doOnClick {
                startAssetBrowser(ticker.market.quote.uid)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.market_option_browse_asset)
            icon = R.drawable.ic_cell_about.contextDrawable()
            subtext = createAssetName(ticker.market.base)
            doOnClick {
                startAssetBrowser(ticker.market.base.uid)
                dismiss()
            }
        }
    }
}

fun Union.showRemoveTradePairDialog(ticker: Ticker) = showBottomDialog {
    val viewModel: MarketViewModel by parentViewModels()
//    title = context.getString(R.string.market_remove_market_dialog)
    title = "Remove Trade Pair"
    subtitle = ticker.market.marketNameOrId
    section {
        cell {
            updatePaddingVerticalV6()
            setTickerStyle()
            bindTicker(ticker)
        }
    }
    button {
        text = context.getString(R.string.button_remove)
        textColor = context.getColor(R.color.component_error)
        doOnClick { viewModel.removeMarket(ticker.market) }
    }
    buttonCancel()
}

fun Union.showAddTradePairDialog() = showBottomDialog {
    val viewModel: MarketViewModel by activityViewModels()
//    title = context.getString(R.string.market_add_market_dialog_title)
    title = "Add Trade Pair"
    section {
        view<ComponentCell> {
            updatePaddingVerticalV6()
            setTickerStyle()
            bindTicker(Ticker.EMPTY)
            viewModel.tickerToAdd.observe(viewLifecycleOwner) {
                if (it != null) bindTicker(it)
            }
        }
        horizontalLayout {
            cell {
                updatePaddingEnd(12.dp)
                title = context.getString(R.string.market_quote)
                field {
                    doAfterTextChanged {
                        viewModel.changeQuoteField(it.toStringOrEmpty())
                    }
                    inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    filters = arrayOf(assetSymbolFilter)
                    showSoftKeyboard()
                    viewModel.isQuoteFieldError.distinctUntilChanged().observe(viewLifecycleOwner) { isError = it }
                }
                layoutWeightLinear = 1f
            }
            cell {
                updatePaddingStart(12.dp)
                title = context.getString(R.string.market_base)
                field {
                    doAfterTextChanged {
                        viewModel.changeBaseField(it.toStringOrEmpty())
                    }
                    inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    filters = arrayOf(assetSymbolFilter)
                    viewModel.isBaseFieldError.distinctUntilChanged().observe(viewLifecycleOwner) { isError = it }
                }
                layoutWeightLinear = 1f
            }
        }
    }
    button {
        text = context.getString(R.string.button_add)
        doOnClick { if (viewModel.addMarket()) dismiss() }
    }
    doOnDismiss(viewModel::resetField)
}

fun Union.showAddMarketGroupDialog() = showBottomDialog {
    val viewModel: MarketViewModel by activityViewModels()
    title = context.getString(R.string.market_add_market_group_dialog_title)
    section {
        cell {
            updatePaddingVerticalV6()
            viewModel.marketGroupToChange.observe(viewLifecycleOwner) {
                bindMarketGroup(it)
            }
        }
        cell {
            title = context.getString(R.string.market_group_asset_symbol)
            field {
                doAfterTextChanged {
                    viewModel.changeMarketGroupField(it.toStringOrEmpty())
                }
                inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                filters = arrayOf(assetSymbolFilter)
                viewModel.isMarketGroupFieldError.observe(viewLifecycleOwner) { isError = it }
            }
            showSoftKeyboard()
        }
    }
    button {
        text = context.getString(R.string.button_add)
        doOnClick { if (viewModel.addMarketGroup()) dismiss() }
    }
    doOnDismiss { viewModel.resetField() }
}