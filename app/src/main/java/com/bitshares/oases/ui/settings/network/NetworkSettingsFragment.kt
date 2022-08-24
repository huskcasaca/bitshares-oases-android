package com.bitshares.oases.ui.settings.network

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bitshares.oases.R
import com.bitshares.oases.database.entities.BitsharesNode
import com.bitshares.oases.extensions.viewbinder.bindNode
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.asset.browser.actionBarLayout
import com.bitshares.oases.ui.asset.browser.actionCoordinatorParams
import com.bitshares.oases.ui.asset.browser.bodyCoordinatorParams
import com.bitshares.oases.ui.base.ContainerFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import modulon.component.appbar.title
import modulon.component.cell.ComponentCell
import modulon.component.cell.buttonStyle
import modulon.component.cell.toggleEnd
import modulon.dialog.button
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.recyclerLayout
import modulon.extensions.viewbinder.verticalLayout
import modulon.layout.lazy.*
import modulon.union.Union

class NetworkSettingsFragment : ContainerFragment() {

//    val viewModel: NodeListViewModel by activityViewModels()

//    override fun ViewGroup.onCreateView() {
//        fitsSystemWindows = true
//        actionBarLayout {
//            layoutParams = actionCoordinatorParams()
//            title("Network")
//            walletStateMenu()
//        }
//        verticalLayout {
//            layoutParams = bodyCoordinatorParams()
//            recyclerLayout {
//                section {
//                    //                header = context.getString(R.string.node_settings_info_title)
//                    cell {
//                        updatePaddingVerticalHalf()
//                        title = context.getString(R.string.node_settings_websocket_state)
//                        //                    title = context.getString(R.string.node_settings_websocket_state)
////                        lifecycleScope.launch {
////                            globalWebsocketManager.state.collectLatest {
////                                @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
////                                subtitle = when (it) {
////                                    GrapheneClient.State.Connecting -> context.getString(R.string.node_settings_websocket_state_connecting)
////                                    GrapheneClient.State.Connected -> context.getString(R.string.node_settings_websocket_state_connected)
////                                    GrapheneClient.State.LoggedIn -> "Logging In" // context.getString(R.string.node_settings_websocket_state_messaging)
////                                    GrapheneClient.State.Closed -> context.getString(R.string.node_settings_websocket_state_closed)
////                                }
////                                subtitleView.textColor = if (it == GrapheneClient.State.Closed) context.getColor(R.color.component_error) else context.getColor(R.color.component)
////                            }
////                        }
//                    }
//                    cell {
//                        updatePaddingVerticalHalf()
//                        title = "Current Node"
////                        lifecycleScope.launch {
////                            viewModel.websocketState.collectLatest {
////                                subtitle = it.config.name
////                            }
////                        }
//                    }
////                cell {
////                    updatePaddingVerticalHalf()
////                    title = "Chain Type"
////                    NodeRepository.currentNode.observe(viewLifecycleOwner) {
////                        subtitle = when (it.chainId) {
////                            ChainConfig.Chain.CHAIN_ID_MAIN_NET -> "Mainnet"
////                            ChainConfig.Chain.CHAIN_ID_TEST_NET -> "Testnet"
////                            else -> "Unknown"
////                        }
////                    }
////                }
//                    //                cell {
//                    //                    Graphene.KEY_CORE_ASSET.observe(activityLifecycleOwner) { text = it.symbol.toString() }
//                    //                }
//                    cell {
//                        updatePaddingVerticalHalf()
//                        text = context.getString(R.string.node_settings_auto_select)
//                        toggleEnd {
//                            viewModel.isAutoSelect.observe(viewLifecycleOwner) {
//                                setChecked(it, true)
//                                //                            subtext = context.getString(if (it) R.string.node_settings_auto_select_enabled else R.string.node_settings_auto_select_disabled)
//                            }
//                        }
//                        doOnClick { viewModel.setAutoSelect() }
//                    }
//                }
//                section {
//                    header = context.getString(R.string.node_settings_api_nodes_title)
//                    list<ComponentCell, BitsharesNode> {
//                        construct { updatePaddingVerticalV8() }
//                        data {
//                            bindNode(it)
//                            doOnClick {
//                                viewModel.setAutoSelect(false)
//                                viewModel.switch(it)
//                            }
//                            doOnLongClick { showNodeEditor(it) }
//                        }
//                        @Suppress("UNCHECKED_CAST")
//                        payload { node, payload ->
//                            payload as WebsocketState
////                            bindNode(node, node.id == payload.selected, node.id == payload.config.id)
//                        }
//                        distinctItemsBy { it.id }
//                        distinctContentBy { it }
////                        lifecycleScope.launch {
////                            viewModel.websocketState.collectLatest { submitPayload(it) }
////                        }
//                        lifecycleScope.launch {
//                            viewModel.nodes.collectLatest { submitList(it) }
//                        }
//
////                    viewModel.nodeStateList
//                    }
//                    cell {
//                        buttonStyle()
//                        title = context.getString(R.string.node_settings_add_node_button)
//                        doOnClick { showNodeEditor(null) }
//                    }
//                }
//                section {
//                    cell {
//                        title = "Call "
//                        doOnClick {
//                            text = "Loading..."
////                            lifecycleScope.launch(Dispatchers.IO) {
////                                globalWebsocketManager.getObject(AccountIdType(0U)).let {
////                                    withContext(Dispatchers.Main.immediate) {
////                                        text = it.toString()
////                                    }
////                                }
////                            }
//                        }
//                    }
//                }
//                logo()
//            }
//        }
//    }
}

//private fun Union.showNodeEditor(node: BitsharesNode?) = showBottomDialog {
//    val viewModel: NodeEditorViewModel by viewModels()
//    title = context.getString(R.string.node_settings_node_info_title)
//    section {
//        cell {
//            updatePaddingVerticalV6()
//            title = context.getString(R.string.node_settings_name)
//            field {
//                hint = context.getString(R.string.node_settings_name_hint)
//                doAfterTextChanged { viewModel.nameField = it.toStringOrEmpty() }
//                viewModel.nodeInEdit.observe(viewLifecycleOwner) { fieldtext = it.name }
//            }
//        }
//        cell {
//            updatePaddingVerticalV6()
//            title = context.getString(R.string.node_settings_url)
//            field {
//                hint = "wss://"
//                inputType = InputTypeExtended.TYPE_URI_NO_SUGGESTION
//                doAfterTextChanged { viewModel.uriField = it.toStringOrEmpty() }
//                viewModel.isUriInvalid.observe(viewLifecycleOwner) {
//                    isError = it
//                    requestFocus()
//                }
//                viewModel.nodeInEdit.observe(viewLifecycleOwner) {
//                    fieldtext = it.url
//                    requestFocus()
//                }
//                showSoftKeyboard()
//            }
//        }
//        cell {
//            updatePaddingVerticalV6()
//            title = context.getString(R.string.node_settings_username)
//            field {
//                hint = context.getString(R.string.node_settings_username_hint)
//                inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
//                doAfterTextChanged { viewModel.usernameField = it.toStringOrEmpty() }
//                viewModel.nodeInEdit.observe(viewLifecycleOwner) { fieldtext = it.username }
//            }
//        }
//        cell {
//            updatePaddingVerticalV6()
//            title = context.getString(R.string.node_settings_password)
//            field {
//                hint = context.getString(R.string.node_settings_password_hint)
//                inputType = InputTypeExtended.TYPE_PASSWORD
//                doAfterTextChanged { viewModel.passwordField = it.toStringOrEmpty() }
//                viewModel.nodeInEdit.observe(viewLifecycleOwner) { fieldtext = it.password }
//            }
//        }
//    }
//    button {
//        text = context.getString(R.string.button_save)
//        doOnClick { if (viewModel.save()) dismiss() }
//    }
//    button {
//        text = context.getString(R.string.button_delete)
//        textColor = context.getColor(R.color.component_error)
//        isVisible = false
//        viewModel.nodeInEdit.observe(viewLifecycleOwner) { isVisible = it.id != 0L }
//        doOnClick {
//            viewModel.remove()
//            dismiss()
//        }
//    }
//    button {
//        text = context.getString(R.string.button_cancel)
//        doOnClick { dismiss() }
//    }
//    viewModel.edit(node)
//}
