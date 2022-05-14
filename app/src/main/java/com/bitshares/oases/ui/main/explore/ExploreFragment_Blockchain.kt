package com.bitshares.oases.ui.main.explore

import android.text.TextUtils
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import bitshareskit.entities.Block
import bitshareskit.operations.Operation
import com.bitshares.oases.R
import com.bitshares.oases.chain.ChainType
import com.bitshares.oases.chain.chainColorResMap
import com.bitshares.oases.chain.chainNameResMap
import com.bitshares.oases.extensions.compat.startBlockBrowser
import com.bitshares.oases.extensions.viewbinder.bindBlock
import com.bitshares.oases.extensions.viewbinder.bindOperation
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.picker.AccountPickerViewModel
import com.bitshares.oases.ui.account.voting.VotingViewModel
import com.bitshares.oases.ui.asset.picker.AssetPickerViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startFragment
import com.bitshares.oases.ui.main.MainViewModel
import com.bitshares.oases.ui.main.search.GlobalSearchFragment
import modulon.component.cell.ComponentCell
import modulon.component.cell.IconSize
import modulon.component.cell.buttonStyle
import modulon.extensions.animation.rotation45
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.text.TABULAR_TRANSFORMATION_METHOD
import modulon.extensions.view.backgroundSelectorColor
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.startScrolling
import modulon.layout.lazy.*
import java.text.DateFormat

class ExploreFragment_Blockchain : ContainerFragment() {

    private val viewModel: ExploreViewModel by activityViewModels()
    private val votingViewModel: VotingViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val accountSearchingViewModel: AccountPickerViewModel by activityViewModels()
    private val assetSearchingViewModel: AssetPickerViewModel by activityViewModels()

    override fun onCreateView() {
        setupRecycler {
            section {
                cell {
//                    backgroundSelectorColor = context.getColor(chainColorResMap.getValue(ChainType.UNKNOWN))
                    viewModel.chainType.observe(viewLifecycleOwner) {
                        backgroundSelectorColor = context.getColor(chainColorResMap.getValue(it))
                    }
                    viewModel.chainType.observe(viewLifecycleOwner) { text = context.getString(chainNameResMap.getValue(it)) }
                    viewModel.chainId.observe(viewLifecycleOwner) { subtext = it }
                    textView.apply {
                        textSize = 18f
                        textColor = context.getColor(R.color.text_primary_inverted)
                    }
                    subtextView.apply {
                        typeface = typefaceMonoRegular
                        textSize = 16f
                        textColor = context.getColor(R.color.text_secondary_inverted)
                        isSingleLine = true
                        ellipsize = TextUtils.TruncateAt.MIDDLE
                    }
                    iconSize = IconSize.SIZE_5
                    text = context.getString(chainNameResMap.getValue(ChainType.UNKNOWN))
                    icon = R.drawable.ic_logo_app.contextDrawable().apply {
                        mutate()
                        setTint(context.getColor(R.color.background))
                    }
                    iconView.rotation45 {
                        viewModel.blockId.distinctUntilChanged().observe(viewLifecycleOwner) {
                            post {
                                start()
                            }
                        }
                    }
                    doOnClick {
                        startFragment<GlobalSearchFragment>()
                    }
                }
            }
            section {
                header = "Chain Info"
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.chain_explore_time)
                    subtitleView.startScrolling()
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    combineNonNull(viewModel.showLocaltime, viewModel.chainTime, viewModel.localTime).observe(viewLifecycleOwner) { (showLocal, chainTime, localTime) ->
                        subtitle = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(if (showLocal) localTime else chainTime)
                    }
                    viewModel.showLocaltime.observe(viewLifecycleOwner) { title = if (it) context.getString(R.string.chain_explore_local_time) else context.getString(R.string.chain_explore_chain_time) }
                    doOnClick { viewModel.showLocaltime.value = !viewModel.showLocaltime.value }
//                                                setLinearHorizontalFilling()
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.chain_explore_next_maintenance_time)
                    subtitleView.startScrolling()
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    viewModel.nextMaintenanceTime.observe(viewLifecycleOwner) {
                        subtitle = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.chain_explore_last_block)
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    viewModel.blockNum.observe(viewLifecycleOwner) {
                        subtitle = it.toString()
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.chain_explore_last_irreversible_block)
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    viewModel.irreversibleBlockNum.observe(viewLifecycleOwner) {
                        subtitle = it.toString()
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.chain_explore_last_block_hash)
                    textView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    subtitleView.ellipsize = TextUtils.TruncateAt.MIDDLE
                    subtitleView.typeface = typefaceMonoRegular
                    viewModel.blockId.observe(viewLifecycleOwner) {
                        subtitle = it
                    }
                    allowMultiLine = true
//                    viewModel.blockTimeOffset.observe(viewLifecycleOwner) {
//                        val sec = it.toInt() / 1000
//                        subtitleView.textWithVisibility = if (sec == 0) context.getString(R.string.chain_explore_block_time_now) else context.getString(R.string.chain_explore_block_time_ago, formatTimeStringFromSec(sec))
//                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.chain_explore_active_witnesses)
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    viewModel.witnessNum.observe(viewLifecycleOwner) {
                        subtitle = it.toString()
                    }
                    doOnClick { viewModel.notifyTab.value = ExploreFragment.Tabs.WITNESS }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.chain_explore_active_committees)
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    viewModel.committeeMemberNum.observe(viewLifecycleOwner) {
                        subtitle = it.toString()
                    }
                    doOnClick { viewModel.notifyTab.value = ExploreFragment.Tabs.COMMITTEE }
                }
            }
            section {
                header = "Statics"
                cell {
                    updatePaddingVerticalHalf()
                    title = "Block Time"
                    viewModel.blockTime.observe {
                        subtitle = "${it / 1000} Seconds"
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = "Transactions Per Block"
                    viewModel.txPerBlock.observe {
                        subtitle = "$it tx"
                    }
                }
            }
            section {
                cell {
                    buttonStyle()
                    updatePaddingVerticalHalf()
                    viewModel.isBlockHistory.observe { title = if (it) "Show Activities" else "Show Blocks" }
                    doOnClick { viewModel.isBlockHistory.value = !viewModel.isBlockHistory.value }
                }
            }
            section {
                header = "Recent Blocks"
                list<ComponentCell, Block> {
                    construct {
                        updatePaddingVerticalHalf()
                    }
                    data {
                        bindBlock(it)
                        doOnClick { startBlockBrowser(it.blockNum) }
                    }
                    distinctItemsBy { it.blockNum }
                    distinctContentBy { it }
                    viewModel.recentBlocksFiltered.observe(viewLifecycleOwner) { submitList(it) }
                }
                isVisible = false
                combineNonNull(viewModel.recentBlocksFiltered, viewModel.isBlockHistory).observe(viewLifecycleOwner) { (list, show) -> isVisible = list.isNotEmpty() && show }

            }
            section {
                header = "Recent Activities"
                list<ComponentCell, Operation> {
                    construct {
                        updatePaddingVerticalHalf()
                    }
                    data {
                        bindOperation(it)
                    }
                    distinctItemsBy { it.hashCode() }
                    distinctContentBy { it }
                    viewModel.opListLive.observe(viewLifecycleOwner) { submitList(it) }
                }
                isVisible = false
                combineNonNull(viewModel.opListLive, viewModel.isBlockHistory).observe(viewLifecycleOwner) { (list, show) -> isVisible = list.isNotEmpty() && !show }
            }
            logo()
        }
    }
}