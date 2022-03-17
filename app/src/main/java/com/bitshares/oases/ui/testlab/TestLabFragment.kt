package com.bitshares.oases.ui.testlab

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import graphene.chain.K102_AccountObject
import graphene.chain.K103_AssetObject
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.oases.netowrk.rpc.GrapheneClient
import com.bitshares.oases.netowrk.rpc.GrapheneNode
import com.bitshares.oases.provider.chain_repo.GrapheneRepository
import com.bitshares.oases.ui.base.ContainerFragment
import graphene.protocol.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
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
import org.java_json.JSONObject

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
                    val committee = """{"id":"1.2.0","membership_expiration_date":"1969-12-31T23:59:59","registrar":"1.2.0","referrer":"1.2.0","lifetime_referrer":"1.2.0","network_fee_percentage":2000,"lifetime_referrer_fee_percentage":8000,"referrer_rewards_percentage":0,"name":"committee-account","owner":{"weight_threshold":1,"account_auths":[],"key_auths":[],"address_auths":[]},"active":{"weight_threshold":48780,"account_auths":[["1.2.121",221],["1.2.282",7163],["1.2.12376",61030],["1.2.25010",7314],["1.2.125824",221],["1.2.413040",226],["1.2.605288",203],["1.2.1120617",7325],["1.2.1634961",558],["1.2.1673567",5984],["1.2.1791523",7314]],"key_auths":[],"address_auths":[]},"options":{"memo_key":"BTS1111111111111111111111111111111114T1Anm","voting_account":"1.2.5","num_witness":0,"num_committee":0,"votes":[],"extensions":[]},"num_committee_voted":120,"statistics":"2.6.0","whitelisting_accounts":["1.2.1530380"],"blacklisting_accounts":["1.2.979278"],"whitelisted_accounts":[],"blacklisted_accounts":[],"owner_special_authority":[0,{}],"active_special_authority":[0,{}],"top_n_control_flags":0}"""
                    section {
                        cell {
                            doOnClick {
                                lifecycleScope.launch {
                                    text = opsSuspend { // 2000k
                                        GRAPHENE_JSON_PLATFORM_SERIALIZER.decodeFromString<K102_AccountObject>(committee)
                                    }
                                }
                            }
                        }
                    }
                    section {
                        cell {
                            doOnClick {
                                lifecycleScope.launch {
                                    text = opsSuspend { // 2000k
                                        AccountObject(JSONObject(committee))
                                    }
                                }
                            }
                        }
                    }
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
                                                val ko = GRAPHENE_JSON_PLATFORM_SERIALIZER.decodeFromString<K102_AccountObject>(o1.rawJson.toString())
                                                viewModel.console(ko.toString())
                                                subtext = ko.toString()
                                            }.onFailure { it.printStackTrace() }
                                        }
                                        ProtocolType.ASSET -> {
                                            val o1 = GrapheneRepository.getObjectOrEmpty<AssetObject>(fieldtext.toLong())
                                            viewModel.console(o1.rawJson.toString(4))
                                            runCatching {
                                                val ko = GRAPHENE_JSON_PLATFORM_SERIALIZER.decodeFromString<K103_AssetObject>(o1.rawJson.toString())
                                                viewModel.console(ko.toString())
                                                subtext = ko.toString()
                                            }.onFailure { it.printStackTrace() }
                                        }
                                        else -> TODO()
                                    }.getOrThrow()
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