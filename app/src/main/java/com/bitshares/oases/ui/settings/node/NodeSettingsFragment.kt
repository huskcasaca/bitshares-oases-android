package com.bitshares.oases.ui.settings.node

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import bitshareskit.chain.ChainConfig
import com.bitshares.oases.R
import com.bitshares.oases.database.entities.Node
import com.bitshares.oases.extensions.viewbinder.bindNode
import com.bitshares.oases.netowrk.java_websocket.WebSocketState
import com.bitshares.oases.provider.local_repo.NodeRepository
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.ComponentCell
import modulon.component.buttonStyle
import modulon.component.toggleEnd
import modulon.dialog.button
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.layout.actionbar.title
import modulon.layout.recycler.*


class NodeSettingsFragment : ContainerFragment() {

    val viewModel: NodeSettingsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            title(context.getString(R.string.node_settings_title))
            walletStateMenu()
//        setupActionBarMenu {
//            addItem {
//                text = context.getString(R.string.menu_add_node)
//                icon = getDrawable(R.drawable.ic_menu_add)
//                doOnClick { showEditNodeDialog(null) }
//            }
//        }
        }
        setupRecycler {
            section {
        //                header = context.getString(R.string.node_settings_info_title)
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.node_settings_websocket_state)
        //                    title = context.getString(R.string.node_settings_websocket_state)
                    viewModel.connectionState.observe(viewLifecycleOwner) {
                        subtitle = when (it) {
                            WebSocketState.CONNECTING -> context.getString(R.string.node_settings_websocket_state_connecting)
                            WebSocketState.CONNECTED -> context.getString(R.string.node_settings_websocket_state_connected)
                            WebSocketState.MESSAGING -> context.getString(R.string.node_settings_websocket_state_messaging)
                            WebSocketState.CLOSED -> context.getString(R.string.node_settings_websocket_state_closed)
                        }
                        subtitleView.textColor = if (it == WebSocketState.CLOSED) context.getColor(R.color.component_error) else context.getColor(R.color.component)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = "Current Node"
                    NodeRepository.currentNode.observe { subtitle = it.name.ifEmpty { it.url } }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = "Chain Type"
                    NodeRepository.currentNode.observe {
                        subtitle = when (it.chainId) {
                            ChainConfig.Chain.CHAIN_ID_MAIN_NET -> "Mainnet"
                            ChainConfig.Chain.CHAIN_ID_TEST_NET -> "Testnet"
                            else -> "Unknown"
                        }
                    }
                }
        //                cell {
        //                    Graphene.KEY_CORE_ASSET.observe(activityLifecycleOwner) { text = it.symbol.toString() }
        //                }
                cell {
                    updatePaddingVerticalHalf()
                    text = context.getString(R.string.node_settings_auto_select)
                    toggleEnd {
                        viewModel.autoSelect.observe(viewLifecycleOwner) {
                            setChecked(it, true)
        //                            subtext = context.getString(if (it) R.string.node_settings_auto_select_enabled else R.string.node_settings_auto_select_disabled)
                        }
                    }
                    doOnClick { viewModel.setAutoSelect() }
                }
            }
            section {
                header = context.getString(R.string.node_settings_api_nodes_title)
                list<ComponentCell, Node> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindNode(it)
                        doOnClick {
                            viewModel.setAutoSelect(false)
                            viewModel.switchNode(it)
                        }
                        doOnLongClick { showEditNodeDialog(it) }
                    }
                    payload { data, payload ->
                        payload as Pair<Int, Int>
                        val isSelected = data.id == payload.first
                        val isCurrent  = data.id == payload.second
                        checkView.backgroundTintColor = context.getColor(if (isSelected && !isCurrent) R.color.component_warning else R.color.component)
                        isChecked = isSelected || isCurrent
                    }
                    distinctItemsBy { it.id }
                    distinctContentBy { it }
                    viewModel.nodeList.observe(viewLifecycleOwner) { adapter.submitList(it) }
                    viewModel.nodeStateList.observe(viewLifecycleOwner) { adapter.submitPayload(it) }
                }
                cell {
                    buttonStyle()
                    title = context.getString(R.string.node_settings_add_node_button)
                    doOnClick { showEditNodeDialog(null) }
                }
                viewModel.nodeList.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
        }
    }

    private fun showEditNodeDialog(node: Node?) = showBottomDialog {
        val viewModel: NodeSettingsViewModel by viewModels()
        title = context.getString(R.string.node_settings_node_info_title)
        section {
            cell {
                updatePaddingVerticalV6()
                title = context.getString(R.string.node_settings_name)
                field {
                    hint = context.getString(R.string.node_settings_name_hint)
                    doAfterTextChanged { viewModel.nodeNameField = it.toStringOrEmpty() }
                    viewModel.currentNodeInEdit.observe(viewLifecycleOwner) { fieldtext = it.name }
                }

            }
            cell {
                updatePaddingVerticalV6()
                title = context.getString(R.string.node_settings_url)
                field {
                    hint = "wss://"
                    inputType = InputTypeExtended.TYPE_URI_NO_SUGGESTION
                    doAfterTextChanged { viewModel.nodeUriField = it.toStringOrEmpty() }
                    viewModel.isUriFieldError.observe(viewLifecycleOwner) {
                        isError = it
                        requestFocus()
                    }
                    viewModel.currentNodeInEdit.observe(viewLifecycleOwner) {
                        fieldtext = it.url
                        requestFocus()
                    }
                    showSoftKeyboard()
                }
            }
            cell {
                updatePaddingVerticalV6()
                title = context.getString(R.string.node_settings_username)
                field {
                    hint = context.getString(R.string.node_settings_username_hint)
                    inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                    doAfterTextChanged { viewModel.nodeUsernameField = it.toStringOrEmpty() }
                    viewModel.currentNodeInEdit.observe(viewLifecycleOwner) { fieldtext = it.username }
                }
            }
            cell {
                updatePaddingVerticalV6()
                title = context.getString(R.string.node_settings_password)
                field {
                    hint = context.getString(R.string.node_settings_password_hint)
                    inputType = InputTypeExtended.TYPE_PASSWORD
                    doAfterTextChanged { viewModel.nodePasswordField = it.toStringOrEmpty() }
                    viewModel.currentNodeInEdit.observe(viewLifecycleOwner) { fieldtext = it.password }
                }
            }
        }
        button {
            text = context.getString(R.string.button_save)
            doOnClick { if (viewModel.saveCurrentNode()) dismiss() }
        }
        button {
            text = context.getString(R.string.button_delete)
            textColor = context.getColor(R.color.component_error)
            isVisible = false
            viewModel.currentNodeInEdit.observe(viewLifecycleOwner) {
                if (it == Node.EMPTY) return@observe
                isVisible = true
                doOnClick {
                    viewModel.removeNode(it)
                    dismiss()
                }
            }
        }
        button {
            text = context.getString(R.string.button_cancel)
            doOnClick { dismiss() }
        }
        viewModel.setCurrentEditNode(node)
    }

}