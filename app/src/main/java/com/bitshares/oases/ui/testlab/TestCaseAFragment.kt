package com.bitshares.oases.ui.testlab

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.oases.R
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.provider.chain_repo.GrapheneRepository
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startFragment
import com.bitshares.oases.ui.intro.IntroFragment
import graphene.chain.K102_AccountObject
import graphene.chain.K103_AssetObject
import graphene.protocol.ImplementationType
import graphene.protocol.ProtocolType
import graphene.serializers.GRAPHENE_JSON_PLATFORM_SERIALIZER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import modulon.component.cell.ComponentCell
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.*
import modulon.widget.PlainTextView

class TestCaseAFragment : ContainerFragment() {
    private val viewModel: TestLabViewModel by activityViewModels()
    override fun ViewGroup.onCreateView() {
        view<LazyListView> {
            section {
                cell {
                    title = "Test FrameLayout"
                    doOnClick {
                        val time = Clock.System.now()
                        lifecycleScope.launch(Dispatchers.IO) {
                            repeat(1000) {
                                create<FrameLayout>()
                            }
                            withContext(Dispatchers.Main) {
                                subtitle = "${(Clock.System.now() - time).inWholeMilliseconds}"
                            }
                        }
                    }
                }
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
            }
            logo()
        }
    }
}

class TestCaseBFragment : ContainerFragment() {
    private val viewModel: TestLabViewModel by activityViewModels()
    override fun ViewGroup.onCreateView() {
        view<LazyListView>  {
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
            logo()
        }
    }
}

class TestCaseCFragment : ContainerFragment() {
    private val viewModel: TestLabViewModel by activityViewModels()
    override fun ViewGroup.onCreateView() {
        view<LazyListView> {
            section {
                cell {
                    val channel = Channel<String>()
                    title = "Ktor Send"
                    doOnClick {
                    }
                }
            }
            section {
                repeat(10_000) {
                    lazyView<ComponentCell> {
                        title = "Index $it"
                        icon = R.drawable.ic_cell_test_lab.contextDrawable()
                        doOnThrottledClick {
                            startFragment<IntroFragment>()
                        }
                    }
                }

            }
            logo()
        }
    }
}

class TestCaseDFragment : ContainerFragment() {
    private val viewModel: TestLabViewModel by activityViewModels()
    override fun ViewGroup.onCreateView() {
        view<LazyListView> {
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
            logo()
        }
    }
}