package com.bitshares.oases.ui.trading

import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.entities.Order
import bitshareskit.extensions.formatAssetBalance
import bitshareskit.extensions.formatAssetBigDecimal
import bitshareskit.extensions.symbolOrId
import bitshareskit.models.SimplePrice
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.text.createAccountSpan
import com.bitshares.oases.extensions.text.createAssetName
import com.bitshares.oases.extensions.viewbinder.feeCell
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.extensions.viewbinder.setTickerItemStyle
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.transaction.bindTransaction
import kotlinx.coroutines.launch
import modulon.UI
import modulon.component.cell.ComponentPaddingCell
import modulon.dialog.section
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.font.typefaceBold
import modulon.extensions.font.typefaceDinMedium
import modulon.extensions.graphics.createRoundRectSelectorDrawable
import modulon.extensions.text.TABULAR_TRANSFORMATION_METHOD
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.lazy.construct
import modulon.layout.lazy.data
import modulon.layout.lazy.list
import modulon.layout.lazy.manager.ReversedLinearLayoutManager
import modulon.layout.lazy.section
import modulon.widget.PlainTextView
import modulon.widget.doOnPercentChanged
import modulon.widget.doOnTrackingTouchChanged

class TradingFragment_AskBid : ContainerFragment() {

    private val tab by lazy { requireArguments().getSerializable(IntentParameters.KEY_TAB_TYPE) as TradingFragment.Tabs }
    private val isBuy by lazy { tab == TradingFragment.Tabs.BUY }
    private val viewModel: TradingViewModel by activityViewModels()

    override fun ViewGroup.onCreateView() {
        recyclerLayout {
            section {
                linearLayout {
                    layoutWidth = MATCH_PARENT
                    orientation = if (viewModel.isHorizontalLayout) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
                    verticalLayout {
                        cell {
                            if (viewModel.isHorizontalLayout) updatePaddingEnd(12.dp)
                            title = context.getString(R.string.market_price)
                            subtitle = context.getString(R.string.market_amount)
                            updatePaddingBottom(2.dp)
                        }
                        frameLayout {
                            layoutWidth = MATCH_PARENT
                            nestedScrollLayout {
                                isNestedScrollingEnabled = false
                                recyclerLayout {
                                    noPadding()
                                    layoutManager = ReversedLinearLayoutManager(context)
                                    // FIXME: 22/1/2022  requestLayout() improperly called by modulon.components.PlainTextView{fee471d V.ED..... ..S..... 254,0-411,60} during second layout pass: posting in next frame
                                    list<ComponentPaddingCell, Order> {
                                        construct {
                                            setTickerItemStyle()
                                            if (viewModel.isHorizontalLayout) updatePaddingEnd(12.dp)
                                        }
                                        data {
                                            titleView.text = it.price.toPlainString()
                                            subtitleView.text = it.quote.toPlainString()
                                            doOnClick { viewModel.setPrice(it.price, isBuy) }
                                        }
                                        viewModel.roundedAsks.observe(viewLifecycleOwner) {
                                            submitList(it)
                                            post { if (!isOnTouch) fullScroll(View.FOCUS_DOWN) }
                                        }
                                    }
                                }
                            }
                            // FIXME: 2022/4/26
                            viewModel.roundedAsks.observe(viewLifecycleOwner) {
//                                layoutParams = linearParams {
//                                    width = MATCH_PARENT
                                    layoutHeight = if (it == null || it.size <= 8) ViewGroup.LayoutParams.WRAP_CONTENT else 180.dp
//                                }
                            }
                        }
                        cell {
                            updatePaddingVerticalHalf()
                            customView = createVerticalLayout {
                                view<PlainTextView> {
                                    gravity = Gravity.START
                                    textSize = 18f
                                    typeface = typefaceDinMedium
                                    viewModel.tickerLive.observe(viewLifecycleOwner) { ticker ->
                                        textWithVisibility = if (!ticker.latest.isNaN()) "${formatAssetBigDecimal(ticker.latest, ticker.base.precision + ticker.quote.precision).toPlainString()} ${ticker.base.symbol}" else EMPTY_SPACE
                                    }
                                }
                                view<PlainTextView> {
                                    isVisible = false
                                    gravity = Gravity.END
                                    textSize = 15f
                                    typeface = typefaceDinMedium
                                }
                            }
                            if (viewModel.isHorizontalLayout) updatePaddingEnd(12.dp)
                            viewModel.tickerLive.observe(viewLifecycleOwner) { ticker ->
                                doOnClick { viewModel.setPrice(ticker.latest.toBigDecimal(), isBuy) }
                            }
                        }
                        frameLayout {
                            nestedScrollLayout {
                                isNestedScrollingEnabled = false
                                recyclerLayout {
                                    noPadding()
                                    list<ComponentPaddingCell, Order> {
                                        construct {
                                            setTickerItemStyle()
                                            if (viewModel.isHorizontalLayout) updatePaddingEnd(12.dp)
                                        }
                                        data {
                                            titleView.text = it.price.toPlainString()
                                            subtitleView.text = it.quote.toPlainString()
                                            doOnClick { viewModel.setPrice(it.price, isBuy) }
                                        }
                                        viewModel.roundedBids.observe(viewLifecycleOwner) {
                                            submitList(it)
                                            postDelayed(320) { if (!isOnTouch) smoothScrollToPosition(0) }
                                        }
                                    }
                                }
                            }
                            layoutWidth = MATCH_PARENT
                            viewModel.roundedAsks.observe(viewLifecycleOwner) {
                                layoutHeight = if (it == null || it.size <= 8) ViewGroup.LayoutParams.WRAP_CONTENT else 180.dp
                            }
                        }
                        spacer()
                        layoutWidth = if (viewModel.isHorizontalLayout) 0 else MATCH_PARENT
                        layoutWeightLinear = if (viewModel.isHorizontalLayout) 1f else 0f
                        viewModel.orderBook.observe(viewLifecycleOwner) {
                            layoutWeightLinear = if (it != null && (it.bids.isNotEmpty() || it.asks.isNotEmpty())) 1f else 0f
//                            requestLayout()
                        }
                    }
                    frameLayout {
                        layoutWidth = if (viewModel.isHorizontalLayout) 0 else MATCH_PARENT
                        layoutWeightLinear = if (viewModel.isHorizontalLayout) 1.1f else 0f
                        nestedScrollLayout {
                            layoutWidth = MATCH_PARENT
                            layoutHeight = MATCH_PARENT
                            verticalLayout {
                                cell {
                                    updatePaddingVertical4()
                                    updatePaddingTop(R.dimen.cell_padding_top.contextDimenPixelSize())
                                    if (viewModel.isHorizontalLayout) updatePadding(left = 2.dp)
                                    title = context.getString(R.string.market_price)
                                    subtitleView.ellipsize = TextUtils.TruncateAt.END
                                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                                    customHorizontal {
                                        field {
                                            inputType = InputTypeExtended.TYPE_NUMBER_DECIMAL
                                            transformationMethod = TABULAR_TRANSFORMATION_METHOD
                                            (if (isBuy) viewModel.buyPriceFieldNoticed else viewModel.sellPriceFieldNoticed).observe(viewLifecycleOwner) { fieldtext = it }
                                            doAfterTextChanged { viewModel.changePriceField(it.toStringOrEmpty(), true, isBuy) }
                                        }
                                        text {
                                            startScrolling()
                                            maxWidth = 84.dp
                                            viewModel.baseAsset.observe(viewLifecycleOwner) { text = createAssetName(it) }
                                        }
                                    }
                                }
                                cell {
                                    updatePaddingVertical4()
                                    if (viewModel.isHorizontalLayout) updatePadding(left = 2.dp)
                                    title = context.getString(R.string.market_amount)
                                    subtitleView.ellipsize = TextUtils.TruncateAt.END
                                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                                    if (!isBuy) viewModel.sellAmountLeft.observe(viewLifecycleOwner) { subtitleView.text = it.values }
                                    customHorizontal {
                                        field {
                                            inputType = InputTypeExtended.TYPE_NUMBER_DECIMAL
                                            transformationMethod = TABULAR_TRANSFORMATION_METHOD
                                            (if (isBuy) viewModel.buyAmountFieldNoticed else viewModel.sellAmountFieldNoticed).observe(viewLifecycleOwner) { fieldtext = it }
                                            doAfterTextChanged { viewModel.changeAmountField(it.toStringOrEmpty(), isFocused, isBuy) }
                                        }
                                        text {
                                            startScrolling()
                                            maxWidth = 84.dp
                                            viewModel.quoteAsset.observe(viewLifecycleOwner) { text = createAssetName(it) }
                                        }
                                    }
                                }
                                cell {
                                    updatePaddingVertical4()
                                    if (viewModel.isHorizontalLayout) updatePadding(left = 2.dp)
                                    layoutTransition = null
                                    title = context.getString(R.string.market_total)
                                    subtitleView.ellipsize = TextUtils.TruncateAt.END
                                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                                    if (isBuy) viewModel.buyTotalLeft.observe(viewLifecycleOwner) { subtitleView.text = it.values }
                                    customHorizontal {
                                        field {
                                            inputType = InputTypeExtended.TYPE_NUMBER_DECIMAL
                                            transformationMethod = TABULAR_TRANSFORMATION_METHOD
                                            (if (isBuy) viewModel.buyTotalFieldNoticed else viewModel.sellTotalFieldNoticed).observe(viewLifecycleOwner) { fieldtext = it }
                                            doAfterTextChanged { viewModel.changeTotalField(it.toStringOrEmpty(), isFocused, isBuy) }
                                        }
                                        text {
                                            startScrolling()
                                            maxWidth = 84.dp
                                            viewModel.baseAsset.observe(viewLifecycleOwner) { text = createAssetName(it) }
                                        }
                                    }
                                }
                                cell {
                                    updatePaddingVertical4()
                                    if (viewModel.isHorizontalLayout) updatePadding(left = 2.dp)
                                    slider {
                                        layoutHeight = 32.dp
                                        layoutWidth = MATCH_PARENT
                                        layoutMarginStart = (-12).dp
                                        layoutMarginEnd = (-12).dp
                                        doOnPercentChanged { percent, fromUser ->
                                            if (fromUser) viewModel.switchRatio(percent.toDouble(), isBuy)
                                        }
                                        doOnTrackingTouchChanged {
                                            viewModel.switchSlider(it, isBuy)
                                        }
                                        (if (isBuy) viewModel.buyProgress else viewModel.sellProgress).observe(viewLifecycleOwner) {
                                            setPercent(it, true)
                                        }
                                        progressActiveColor = if (isBuy) context.getColor(R.color.component) else context.getColor(R.color.component_error)
                                        thumbColor = if (isBuy) context.getColor(R.color.component) else context.getColor(R.color.component_error)
                                    }
                                }
                                view<ComponentPaddingCell> {
                                    updatePaddingVertical4()
                                    if (viewModel.isHorizontalLayout) updatePadding(left = 2.dp)
                                    frameLayout {
                                        layoutWidth = MATCH_PARENT
                                        background = createRoundRectSelectorDrawable(context.getColor(R.color.component_cover), if (isBuy) context.getColor(R.color.component) else context.getColor(R.color.component_error), UI.CORNER_RADIUS.dpf / 2)
                                        view(textView) {
                                            parentViewGroupOrNull?.removeView(this)
                                            textView.typeface = textView.typefaceBold
                                            textView.textColor = context.getColor(R.color.text_white)
                                            textView.text = context.getString(if (isBuy) R.string.market_buy else R.string.market_sell).toUpperCase()

                                            layoutMarginStart = context.resources.getDimensionPixelSize(modulon.R.dimen.cell_padding_start)
                                            layoutMarginTop = 8.dp
                                            layoutMarginEnd = context.resources.getDimensionPixelSize(modulon.R.dimen.cell_padding_end)
                                            layoutMarginBottom = 8.dp
                                            layoutGravityFrame = Gravity.CENTER
                                        }
                                        doOnClick {
                                            viewModel.isBuy = isBuy
                                            lifecycleScope.launch {
                                                // if (startPermissionCheck(Permission.ACTIVE)) showLimitOrderCreateDialog(isBuy)
                                                showLimitOrderCreateDialog(isBuy)
                                            }
                                        }
                                    }
                                    viewModel.quoteAsset.observe(viewLifecycleOwner) { text = "${context.getString(if (isBuy) R.string.market_buy else R.string.market_sell).toUpperCase()} ${it.symbolOrId}" }
                                }
                                spacer()
                            }
                        }
                    }
                    backgroundTintColor = R.color.background_component.contextColor()
                }
            }
            section {
        //                title = context.getString(R.string.market_market_history)
                header = "History"
                cell {
                }
                isVisible = false
            }
            logo()
        }
    }

    private fun showLimitOrderCreateDialog(isBuy: Boolean) = showBottomDialog {
        bindTransaction(viewModel.buildTransaction(), viewModel)
        section {
            cell {
                updatePaddingVerticalV6()
                title = context.getString(R.string.operation_limit_order_create_seller)
                viewModel.limitOrderCreateOperation.observe(viewLifecycleOwner) { subtitle = createAccountSpan(it.account) }
            }
            cell {
                updatePaddingVerticalV6()
                title = context.getString(R.string.operation_limit_order_create_price)
                viewModel.limitOrderCreateOperation.observe(viewLifecycleOwner) { subtitle = if (isBuy) SimplePrice(it.sells, it.receives).toString() else SimplePrice(it.receives, it.sells).toString() }
            }
            cell {
                updatePaddingVerticalV6()
                title = if (isBuy) context.getString(R.string.operation_limit_order_create_buy) else context.getString(R.string.operation_limit_order_create_sells)
                viewModel.limitOrderCreateOperation.observe(viewLifecycleOwner) { subtitle = formatAssetBalance(if (isBuy) it.receives else it.sells) }
            }
            cell {
                updatePaddingVerticalV6()
                title = if (isBuy) context.getString(R.string.operation_limit_order_create_pay) else context.getString(R.string.operation_limit_order_create_receives)
                viewModel.limitOrderCreateOperation.observe(viewLifecycleOwner) { subtitle = formatAssetBalance(if (isBuy) it.sells else it.receives) }
            }
            feeCell(union, viewModel.transactionBuilder)
        }
    }


}