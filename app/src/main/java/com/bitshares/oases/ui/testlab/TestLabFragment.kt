package com.bitshares.oases.ui.testlab

import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import graphene.chain.K102_AccountObject
import graphene.chain.K103_AssetObject
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.oases.R
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.provider.chain_repo.GrapheneRepository
import com.bitshares.oases.ui.asset.browser.actionBarLayout
import com.bitshares.oases.ui.asset.browser.bodyCoordinatorParams
import com.bitshares.oases.ui.asset.browser.actionCoordinatorParams
import com.bitshares.oases.ui.base.ContainerFragment
import graphene.protocol.*
import graphene.serializers.GRAPHENE_JSON_PLATFORM_SERIALIZER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import modulon.component.ComponentCell
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.linearLayout
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.layout.actionbar.subtitle
import modulon.layout.actionbar.title
import modulon.layout.recycler.*
import modulon.layout.tab.tab
import modulon.widget.PlainTextView

class TestLabFragment : ContainerFragment() {

    private val viewModel: TestLabViewModel by activityViewModels()

    fun Any.console() = viewModel.console(System.currentTimeMillis(), this)

    override fun ViewGroup.onCreateView() {
        fitsSystemWindows = true
        actionBarLayout {
            layoutParams = actionCoordinatorParams()
            title(context.getString(R.string.about_title))
            subtitle(AppConfig.APP_NAME)
        }
        linearLayout {
            layoutParams = bodyCoordinatorParams()
            tabLayout {
                tab { text = "General" }
                tab { text = "Serialization" }
                tab { text = "Ktor Test" }
                tab { text = "Console" }
                post { attachViewPager2(nextView()) }
            }
            pagerLayout {
                post { setCurrentItem(1, false) }
                page<RecyclerLayout> {
                    section {
                        cell {
                            title = "Test Reflect Independent"
                            doOnClick {
                                val time = Clock.System.now()
                                lifecycleScope.launch(Dispatchers.IO) {
                                    repeat(100000) {
                                        create<PlainTextView>()
                                    }
                                    withContext(Dispatchers.Main) {
                                        subtitle = "${(Clock.System.now() - time).inWholeMilliseconds}"
                                    }
                                }
                            }
                        }
                        cell {
                            title = "Test Non Reflect "
                            doOnClick {
                                val time = Clock.System.now()
                                lifecycleScope.launch(Dispatchers.IO) {
                                    repeat(100000) {
                                        PlainTextView(context)
                                    }
                                    withContext(Dispatchers.Main) {
                                        subtitle = "${(Clock.System.now() - time).inWholeMilliseconds}"
                                    }
                                }
                            }
                        }

                        cell {
                            title = "Test All"
                        }

                    }
                    section {
                        cell {
                            val channel = Channel<String>()
                            title = "Ktor Send"
                            doOnClick {
                            }
                        }
                        linearLayout {
                            layoutHeight = 100.dp
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

                    section {
                        cell {
                            title = "Test K102"
                            viewModel.info[ProtocolType.ACCOUNT]?.observe {
                                subtext = it
                            }
                            doOnClick {
                                viewModel.testK102()
                                doOnClick {
                                    viewModel.stopK102()
                                }

                            }
                        }
                    }
                    section {
                        cell {
                            title = "Test K103"
                            viewModel.info[ProtocolType.ASSET]?.observe {
                                subtext = it
                            }
                            doOnClick {
                                viewModel.testK103()
                                doOnClick {
                                    viewModel.stopK103()
                                }

                            }
                        }
                    }

                }
                page<RecyclerLayout> {
                    section {


                    }
                    section {
                        cell {
                            val channel = Channel<String>()
                            title = "Ktor Send"
                            doOnClick {
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