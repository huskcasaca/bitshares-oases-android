package com.bitshares.android.ui.testlab

//import bitshareskit.objects_k.emptyKGrapheneObject
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.ks_object_base.KObjectSpaceType
import bitshareskit.ks_objects.K102AccountObject
import bitshareskit.ks_objects.K103AssetObject
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.android.netowrk.rpc.GrapheneClient
import com.bitshares.android.netowrk.rpc.GrapheneNode
import com.bitshares.android.provider.chain_repo.GrapheneRepository
import com.bitshares.android.ui.base.ContainerFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import modulon.component.ComponentCell
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.text.appendSimpleSpan
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.layout.actionbar.title
import modulon.layout.recycler.*
import modulon.layout.recycler.containers.submitList
import modulon.layout.tab.tab

class TestLabFragment : ContainerFragment() {

    private val viewModel: TestLabViewModel by activityViewModels()

    fun Any.console() = viewModel.console(System.currentTimeMillis(), this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            title("TestLab")
            networkStateMenu()
        }
        setupVertical {
            tabLayout {
                tab { text = "Ktor Test" }
                tab { text = "Object Test" }
                tab { text = "Console" }
                post { attachViewPager2(nextView()) }
            }
            pagerLayout {
                page<RecyclerLayout> {
                    section {
                        cell {
                            title = "TEST STRING IS HERE appendSimpleSpan"
                            doOnClick {
                                GrapheneClient.ClientJson.decodeFromJsonElement<Boolean>(JsonPrimitive(true)).console()
                //                                GrapheneClient.ClientJson.decodeFromJsonElement<ULong>(JsonPrimitive(11231231100)).console()
                //                                GrapheneClient.ClientJson.decodeFromJsonElement<UInt>(JsonPrimitive(1321230)).console()
                //                                GrapheneClient.ClientJson.decodeFromJsonElement<UShort>(JsonPrimitive(100)).console()
                            }
                        }
                        cell {
                            title = buildContextSpannedString {
                                appendSimpleSpan("TEST STRING IS HERE")
                            }
                            doOnClick {  }
                        }
                        cell {
                            val channel = Channel<String>()
                            title = "Ktor Send"
                            doOnClick {
                //                                lifecycleScope.launch {
                //                                    while (true) {
                //                                        viewModel.console(channel.receive())
                //                                    }
                //                                }
                //                                lifecycleScope.launch {
                //                                    while(true) {
                //                                        channel.trySend(System.currentTimeMillis().toString())
                //                                        delay(100)
                //                                    }
                //                                }
                                val client = GrapheneClient(GrapheneNode("GDEX", "wss://testnet.xbts.io/ws"))
                                lifecycleScope.launch { client.start() }
                            }
                        }
                    }
                }
                page<RecyclerLayout> {
                    section {
                        header = "Graphene Object Test"
                        cell {
                            title = "Object Type"
                            viewModel.objectType.observe { subtitle = it.toString() }
                            doOnClick {
                                showBottomDialog {
                                    title = "Select Object Type"
                                    section {
                                        KObjectSpaceType.values().forEach {
                                            cell {
                                                title = it.toString()
                                                doOnClick {
                                                    viewModel.objectType.value = it
                                                    dismissNow()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        cell {
                            val decoder = Json { ignoreUnknownKeys = true }
                            title = "Instance to Fetch"
                            var fieldtext = ""
                            field {
                                doAfterTextChanged { fieldtext = text.toStringOrEmpty() }
                            }
                            doOnClick {
                                if (fieldtext.toLongOrNull() != null) lifecycleScope.launch {
                                    when (viewModel.objectType.value) {
                                        KObjectSpaceType.NULL_OBJECT -> TODO()
                                        KObjectSpaceType.BASE_OBJECT -> TODO()
                                        KObjectSpaceType.ACCOUNT_OBJECT -> {
                                            val o1 = GrapheneRepository.getObjectOrEmpty<AccountObject>(fieldtext.toLong())
                                            viewModel.console(o1.rawJson.toString(4))
                                            runCatching {
                                                val ko = decoder.decodeFromString<K102AccountObject>(o1.rawJson.toString())
                                                viewModel.console(ko.toString())
                                                subtext = ko.toString()
                                            }.onFailure { it.printStackTrace() }
                                        }
                                        KObjectSpaceType.ASSET_OBJECT -> {
                                            val o1 = GrapheneRepository.getObjectOrEmpty<AssetObject>(fieldtext.toLong())
                                            viewModel.console(o1.rawJson.toString(4))
                                            runCatching {
                                                val ko = decoder.decodeFromString<K103AssetObject>(o1.rawJson.toString())
                                                viewModel.console(ko.toString())
                                                subtext = ko.toString()
                                            }.onFailure { it.printStackTrace() }
                                        }
                                        KObjectSpaceType.FORCE_SETTLEMENT_OBJECT -> TODO()
                                        KObjectSpaceType.COMMITTEE_MEMBER_OBJECT -> TODO()
                                        KObjectSpaceType.WITNESS_OBJECT -> TODO()
                                        KObjectSpaceType.LIMIT_ORDER_OBJECT -> TODO()
                                        KObjectSpaceType.CALL_ORDER_OBJECT -> TODO()
                                        KObjectSpaceType.CUSTOM_OBJECT -> TODO()
                                        KObjectSpaceType.PROPOSAL_OBJECT -> TODO()
                                        KObjectSpaceType.OPERATION_HISTORY_OBJECT -> TODO()
                                        KObjectSpaceType.WITHDRAW_PERMISSION_OBJECT -> TODO()
                                        KObjectSpaceType.VESTING_BALANCE_OBJECT -> TODO()
                                        KObjectSpaceType.WORKER_OBJECT -> TODO()
                                        KObjectSpaceType.BALANCE_OBJECT -> TODO()
                                        KObjectSpaceType.HTLC_OBJECT -> TODO()
                                        KObjectSpaceType.CUSTOM_AUTHORITY_OBJECT -> TODO()
                                        KObjectSpaceType.TICKET_OBJECT -> TODO()
                                        KObjectSpaceType.LIQUIDITY_POOL_OBJECT -> TODO()
                                        KObjectSpaceType.GLOBAL_PROPERTY_OBJECT -> TODO()
                                        KObjectSpaceType.DYNAMIC_GLOBAL_PROPERTY_OBJECT -> TODO()
                                        KObjectSpaceType.ASSET_DYNAMIC_DATA -> TODO()
                                        KObjectSpaceType.ASSET_BITASSET_DATA -> TODO()
                                        KObjectSpaceType.ACCOUNT_BALANCE_OBJECT -> TODO()
                                        KObjectSpaceType.ACCOUNT_STATISTICS_OBJECT -> TODO()
                                        KObjectSpaceType.TRANSACTION_OBJECT -> TODO()
                                        KObjectSpaceType.BLOCK_SUMMARY_OBJECT -> TODO()
                                        KObjectSpaceType.ACCOUNT_TRANSACTION_HISTORY_OBJECT -> TODO()
                                        KObjectSpaceType.BLINDED_BALANCE_OBJECT -> TODO()
                                        KObjectSpaceType.CHAIN_PROPERTY_OBJECT -> TODO()
                                        KObjectSpaceType.WITNESS_SCHEDULE_OBJECT -> TODO()
                                        KObjectSpaceType.BUDGET_RECORD_OBJECT -> TODO()
                                        KObjectSpaceType.SPECIAL_AUTHORITY_OBJECT -> TODO()
                                        KObjectSpaceType.BUYBACK_OBJECT -> TODO()
                                        KObjectSpaceType.FBA_ACCUMULATOR_OBJECT -> TODO()
                                        KObjectSpaceType.COLLATERAL_BID_OBJECT -> TODO()
                                        KObjectSpaceType.ORDER_HISTORY_OBJECT -> TODO()
                                        KObjectSpaceType.BUCKET_OBJECT -> TODO()
                                    }
                                }
                            }
                        }
                    }
                }
                page<RecyclerLayout> {
                    section {
                        list<ComponentCell, Pair<String, String>> {
                            construct { updatePaddingVerticalV6() }
                            data {
                                title = it.first
                                subtext = it.second
                            }
                            viewModel.consoleFlowHeader.observe {
                                submitList(it.reversed())
                            }
                        }
                    }
                }
            }
        }

    }

}