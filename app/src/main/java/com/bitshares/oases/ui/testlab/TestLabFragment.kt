package com.bitshares.oases.ui.testlab

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.ks_object_base.ImplementationType
import bitshareskit.ks_object_base.ObjectType
import bitshareskit.ks_object_base.ProtocolType
import bitshareskit.ks_objects.K102AccountObject
import bitshareskit.ks_objects.K103AssetObject
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import bitshareskit.objects.ProposalObject
import com.bitshares.oases.netowrk.rpc.GrapheneClient
import com.bitshares.oases.netowrk.rpc.GrapheneNode
import com.bitshares.oases.provider.chain_repo.GrapheneRepository
import com.bitshares.oases.ui.base.ContainerFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import modulon.component.ComponentCell
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
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
                post { setCurrentItem(1, false) }
                page<RecyclerLayout> {
                    section {
                        cell {
                            val channel = Channel<String>()
                            title = "Ktor Send"
                            doOnClick {
                                val client = GrapheneClient(GrapheneNode("BTSGO", "wss://api.btsgo.net/ws"))
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
                                        ProtocolType.values().forEach {
                                            cell {
                                                title = it.toString()
                                                doOnClick {
                                                    viewModel.objectType.value = it
                                                    dismissNow()
                                                }
                                            }
                                        }
                                        ImplementationType.values().forEach {
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
                                inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                            }
                            doOnClick {
                                if (fieldtext.toLongOrNull() != null) lifecycleScope.launch {
                                    when (viewModel.objectType.value) {
                                        ProtocolType.NULL -> TODO()
                                        ProtocolType.BASE -> TODO()
                                        ProtocolType.ACCOUNT -> {
                                            val o1 = GrapheneRepository.getObjectOrEmpty<AccountObject>(fieldtext.toLong())
                                            viewModel.console(o1.rawJson.toString(4))
                                            runCatching {
                                                val ko = decoder.decodeFromString<K102AccountObject>(o1.rawJson.toString())
                                                viewModel.console(ko.toString())
                                                subtext = ko.toString()
                                            }.onFailure { it.printStackTrace() }
                                        }
                                        ProtocolType.ASSET -> {
                                            val o1 = GrapheneRepository.getObjectOrEmpty<AssetObject>(fieldtext.toLong())
                                            viewModel.console(o1.rawJson.toString(4))
                                            runCatching {
                                                val ko = decoder.decodeFromString<K103AssetObject>(o1.rawJson.toString())
                                                viewModel.console(ko.toString())
                                                subtext = ko.toString()
                                            }.onFailure { it.printStackTrace() }
                                        }
                                        else -> TODO()
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