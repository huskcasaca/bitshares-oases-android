package com.bitshares.oases.ui.transaction.operation_browser

import androidx.fragment.app.activityViewModels
import com.bitshares.oases.chain.formatCoreAssetBalance
import com.bitshares.oases.chain.operationNameStringResMap
import com.bitshares.oases.extensions.compat.startBlockBrowser
import com.bitshares.oases.extensions.viewbinder.bindOperationDescription
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.text.appendDateTimeInstance
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.section
import java.text.DateFormat

class OperationInfoFragment : ContainerFragment() {

    private val viewModel: OperationViewModel by activityViewModels()

    override fun onCreateView() {

        setupRecycler {
            section {
                cell {
                    updatePaddingVerticalHalf()
                    title = "Operation Type"
                    viewModel.operationType.observe(viewLifecycleOwner) {
                        subtitle = context.getString(operationNameStringResMap.getValue(it))
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    subtitleView.typeface = typefaceMonoRegular
                    title = "Create Time"
                    viewModel.operation.observe(viewLifecycleOwner) {
                        subtitle = buildContextSpannedString { appendDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, it.createTime) }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = "Description"
                    subtitleView.isSingleLine = false
                    viewModel.operation.observe(viewLifecycleOwner) {
                        bindOperationDescription(it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = "Fee"
                    viewModel.operation.observe(viewLifecycleOwner) {
                        subtitle = formatCoreAssetBalance(it.fee.amount)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = "Block Height"
                    viewModel.operation.observe(viewLifecycleOwner) {
                        subtitle = it.blockHeight.toString()
                        doOnClick { startBlockBrowser(it.blockHeight) }
                    }
                }
            }
        }

    }
}
