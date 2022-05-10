package com.bitshares.oases.ui.block

import android.text.TextUtils
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.extensions.compat.startAccountBrowser
import com.bitshares.oases.extensions.text.createAccountSpan
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.cell.buttonStyle
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.section
import java.text.DateFormat

class BlockBrowserFragment_Info : ContainerFragment() {

    private val viewModel: BlockViewModel by activityViewModels()

    override fun onCreateView() {

        setupRecycler {
            section {
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.block_info_block_height)
                    viewModel.block.observe(viewLifecycleOwner) {
                        subtitle = it.blockNum.toString()
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
//                    subtitleView.typeface = typefaceMonoRegular
                    title = context.getString(R.string.block_info_block_time)
                    viewModel.block.observe(viewLifecycleOwner) {
                        subtitle = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(it.timestamp)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
//                    subtitleView.typeface = typefaceMonoRegular
                    title = context.getString(R.string.block_info_witness)
                    viewModel.witness.observe(viewLifecycleOwner) {
                        subtitleView.isSingleLine = false
                        subtitle = createAccountSpan(it.witnessAccount)
                        doOnClick { startAccountBrowser(it.witnessAccount.uid) }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    subtitleView.typeface = typefaceMonoRegular
                    title = context.getString(R.string.block_info_transaction_count)
                    viewModel.block.observe(viewLifecycleOwner) {
                        subtitle = it.transactionCount.toString()
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    subtitleView.typeface = typefaceMonoRegular
                    title = context.getString(R.string.block_info_operation_count)
                    viewModel.block.observe(viewLifecycleOwner) {
                        subtitle = it.operationCount.toString()
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.block_info_witness_signature)
                    subtitleView.apply {
                        ellipsize = TextUtils.TruncateAt.MIDDLE
                        typeface = typefaceMonoRegular
                    }
                    viewModel.block.observe(viewLifecycleOwner) {
                        subtitle = it.witnessSignature
                    }
                    allowMultiLine = true
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.block_info_previous_block_hash)
                    subtitleView.apply {
                        ellipsize = TextUtils.TruncateAt.MIDDLE
                        typeface = typefaceMonoRegular
                    }
                    viewModel.block.observe(viewLifecycleOwner) {
                        subtitle = it.previousHash
                    }
                    allowMultiLine = true
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.block_info_root_hash)
                    subtitleView.apply {
                        ellipsize = TextUtils.TruncateAt.MIDDLE
                        typeface = typefaceMonoRegular
                    }
                    viewModel.block.observe(viewLifecycleOwner) {
                        subtitle = it.rootHash
                    }
                    allowMultiLine = true
                }
            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.block_info_previous_block)
                    viewModel.block.observe(viewLifecycleOwner) {
                        doOnClick {
                            viewModel.blockNum.value = viewModel.blockNum.value - 1L
                        }
                    }
                }
            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.block_info_next_block)
                    viewModel.block.observe(viewLifecycleOwner) {
                        doOnClick {
                            viewModel.setBlockNumber(it.blockNum + 1L)
                        }
                    }
                }
            }
            logo()
        }
    }

}
