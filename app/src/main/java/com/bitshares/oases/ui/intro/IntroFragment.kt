package com.bitshares.oases.ui.intro

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bitshares.oases.R
import com.bitshares.oases.chain.accountNameFilter
import com.bitshares.oases.extensions.compat.startImport
import com.bitshares.oases.extensions.text.StringFilter
import com.bitshares.oases.extensions.text.createStringFilterHint
import com.bitshares.oases.extensions.viewbinder.bindAccountV3
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.ui.account.importer.ImportViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.faucet.FaucetViewModel
import com.bitshares.oases.ui.faucet.showFaucetRegisterDialog
import com.bitshares.oases.ui.faucet.showFaucetSelectDialog
import com.bitshares.oases.ui.settings.showLanguageSettingDialog
import com.bitshares.oases.ui.wallet.startWalletUnlock
import bitshareskit.chain.Authority
import kotlinx.coroutines.launch
import modulon.component.IconSize
import modulon.dialog.button
import modulon.dialog.doOnDismiss
import modulon.dialog.section
import modulon.extensions.animation.alpha
import modulon.extensions.animation.animationSet
import modulon.extensions.animation.translate
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.compat.activity
import modulon.extensions.compat.finish
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.font.typefaceBold
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.graphics.createRoundSelectorDrawable
import modulon.extensions.graphics.tint
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.text.appendColored
import modulon.extensions.text.appendTag
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.actionbar.ActionBarLayout
import modulon.layout.actionbar.actionMenu
import modulon.layout.actionbar.menu
import modulon.union.Union
import modulon.widget.FloatingButton
import modulon.widget.PlainTextView

class IntroFragment : ContainerFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCoordinator {
            backgroundTintColor = context.getColor(R.color.background)
            scrollLayout {
                setFrameParamsFill()
                isFillViewport = true
                verticalLayout {
                    view<ImageView> {
                        imageDrawable = R.drawable.logo_alpha_animated.contextDrawable().apply {
                            mutate()
                            setTint(context.getColor(R.color.cell_text_primary))
                            post { (this as AnimatedVectorDrawable).start() }
                        }
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        adjustViewBounds = true
                        updatePadding(left = 96.dp, right = 96.dp, top = 32.dp, bottom = 32.dp)
                        layoutWidth = MATCH_PARENT
                        layoutHeight = 0
                        layoutWeightLinear = 1f
                        animation = animationSet {
                            translate(fromYDelta = (-200).dpf) {
                                duration = 360L
                                interpolator = DecelerateInterpolator()
                            }
                            post { start() }
                        }
                    }
                    verticalLayout {
                        view<PlainTextView> {
                            text = "Welcome to BitShares"
                            textSize = 28f
                            typeface = typefaceBold
                            gravity = Gravity.CENTER_HORIZONTAL
                            setParamsRow()
                        }
                        view<PlainTextView> {
                            text = "Your Decentralized Platform"
                            textSize = 20f
                            gravity = Gravity.CENTER_HORIZONTAL
                            setParamsRow()
                        }
                        updatePadding(top = 20.dp, bottom = 20.dp, left = 20.dp, right = 20.dp)
                        animation = animationSet {
                            translate(fromYDelta = 200.dpf)
                            alpha(toAlpha = 1f)
                            duration = 360L
                            interpolator = DecelerateInterpolator()
                            post { start() }
                        }
                    }
                    frameLayout {
                        noClipping()
                        view<FloatingButton> {
                            icon = R.drawable.ic_test_continue.contextDrawable().apply {
                                tint(R.color.background.contextColor())
                            }
                            iconSize = IconSize.SMALL
                            // TODO: 2022/2/24 replace with component colr
                            background = createRoundSelectorDrawable(56.dp, context.getColor(modulon.R.color.cell_text_primary))
                            layoutGravityFrame = Gravity.CENTER
                            doOnClick {
                                showContinueDialog()
                            }
                        }
                        updatePadding(top = 20.dp, bottom = 20.dp, left = 48.dp, right = 48.dp)
                        setLinearParamsFill(height = 0.dp, weight = 0.4f)
                        animation = animationSet {
                            translate(fromYDelta = 200.dpf)
                            alpha(toAlpha = 1f)
                            duration = 360
                            interpolator = DecelerateInterpolator()
                            post { start() }
                        }
                    }
                }
            }
            viewRow<ActionBarLayout> {
                backgroundTintColor = context.getColor(R.color.transparent)
                actionMenu {
                    icon = R.drawable.ic_cell_cross.contextDrawable()
                    doOnClick { finish() }
                }
                menu {
                    icon = R.drawable.ic_cell_language.contextDrawable()
                    doOnClick { showLanguageSettingDialog() }
                }
            }
        }
    }
}

fun Union.showContinueDialog() = showBottomDialog {
    title = "Continue"
    button {
        text = "Login"
        doOnClick { showLoginDialog() }
    }
    button {
        text = "Create Account"
        doOnClick { showRegisterDialog() }
    }
    button {
        text = "Import..."
        doOnClick { startImport() }
    }
}

fun Union.showLoginDialog() = showBottomDialog {
    val viewModel: ImportViewModel by viewModels()
    title = "Login"
    section {
        verticalLayout {
            viewModel.accountList.observe(viewLifecycleOwner) {
                removeAllViews()
                it.forEach { cell { bindAccountV3(it, true, IconSize.COMPONENT_0) } }
            }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.import_account)
            field {
                inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                isSingleLine = false
                typeface = typefaceMonoRegular
                filters = arrayOf(accountNameFilter)
                doAfterTextChanged { viewModel.changeAccountText(it) }
            }
            viewModel.checkingState.observe(viewLifecycleOwner) { state ->
                subtitleView.isTextError = state == ImportViewModel.State.INVALID_NAME || state == ImportViewModel.State.NETWORK_ERROR
                subtitle = when (state) {
                    ImportViewModel.State.EMPTY -> EMPTY_SPACE
                    ImportViewModel.State.CHECKING -> context.getString(R.string.import_checking)
                    ImportViewModel.State.NETWORK_ERROR -> context.getString(R.string.import_network_error)
                    ImportViewModel.State.INVALID_NAME -> context.getString(R.string.import_invalid_account_name_or_id)
                    ImportViewModel.State.INVALID_SECRET -> EMPTY_SPACE
                    ImportViewModel.State.COMPLETE -> EMPTY_SPACE
                }
            }
        }
        cell {
            updatePaddingVerticalV6()
                title = context.getString(com.bitshares.oases.R.string.import_password)
                field {
                    inputType = EditorInfo.TYPE_TEXT_VARIATION_FILTER or android.view.inputmethod.EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    typeface = typefaceMonoRegular
                    isSingleLine = false
                    doAfterTextChanged { viewModel.changePasswordText(it) }
                }
                viewModel.checkingState.observe(viewLifecycleOwner) { state ->
                    subtitleView.isTextError = state == ImportViewModel.State.INVALID_SECRET
                    subtitle = when (state) {
                        ImportViewModel.State.EMPTY -> EMPTY_SPACE
                        ImportViewModel.State.CHECKING -> EMPTY_SPACE
                        ImportViewModel.State.NETWORK_ERROR -> EMPTY_SPACE
                        ImportViewModel.State.INVALID_NAME -> EMPTY_SPACE
                        ImportViewModel.State.INVALID_SECRET -> context.getString(com.bitshares.oases.R.string.import_invalid_password)
                        ImportViewModel.State.COMPLETE -> EMPTY_SPACE
                    }
                }
                viewModel.cloudPermissions.observe(viewLifecycleOwner) {
                    if (viewModel.checkingState.value == com.bitshares.oases.ui.account.importer.ImportViewModel.State.COMPLETE) {
                        subtitle = buildContextSpannedString {
                            if (it.contains(Authority.OWNER)) appendTag(context.getString(R.string.tag_owner), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                            if (it.contains(Authority.ACTIVE)) appendTag(context.getString(R.string.tag_active), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                            if (it.contains(Authority.MEMO)) appendTag(context.getString(R.string.tag_memo), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                        }
                    }
                }
        }
    }
    button {
        text = context.getString(R.string.import_import_button)
        textColor = context.getColor(R.color.component)
        NetworkService.isConnectedLive.observe(viewLifecycleOwner) {
            isClickable = it
            textColor = context.getColor(if (it) R.color.component else R.color.component_disabled)
            doOnClick { lifecycleScope.launch { if (viewModel.checkForImport() && startWalletUnlock() && viewModel.import()) activity.finish() } }
        }
    }
    showSoftKeyboard()
}

fun Union.showRegisterDialog() = showBottomDialog {
    val viewModel: FaucetViewModel by activityViewModels()
    title = "Create Account"
    section {
        cell {
            title = context.getString(R.string.faucet_register_faucet)
            viewModel.faucet.observe(viewLifecycleOwner) {
                subtitle = it.url
            }
            doOnClick { showFaucetSelectDialog() }
        }
        cell {
            title = context.getString(R.string.faucet_register_account)
            field {
                filters = arrayOf(accountNameFilter)
                inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                typeface = typefaceMonoRegular
                isSingleLine = false
                doAfterTextChanged { viewModel.changeNameField(it.toStringOrEmpty()) }
                viewModel.isAccountNameFieldError.observe(viewLifecycleOwner) { isError = it }
            }
            combineNonNull(viewModel.accountExist, viewModel.accountNameField).observe(viewLifecycleOwner) { (isExist, field) ->
                subtextView.textWithVisibility = buildContextSpannedString {
                    if (isExist) {
                        appendColored(context.getString(R.string.faucet_register_account_exist), context.getColor(R.color.component_error))
                    } else {
                        append(createStringFilterHint(field, StringFilter.FILTER_CHEAP_ACCOUNT_NAME))
                    }
                }
                if (field.isEmpty()) subtextView.isVisible = false
            }
        }
        cell {
            title = context.getString(R.string.faucet_register_password)
            field {
                inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                typeface = typefaceMonoRegular
                isSingleLine = false
                doAfterTextChanged { viewModel.changePasswordField(it.toStringOrEmpty()) }
                viewModel.isAccountPasswordFieldError.observe(viewLifecycleOwner) { isError = it }
                viewModel.accountPasswordFieldNoticed.observe(viewLifecycleOwner) { fieldtext = it.toStringOrEmpty() }
            }
            viewModel.accountPasswordField.observe(viewLifecycleOwner) {
                subtextView.textWithVisibility = createStringFilterHint(it, StringFilter.FILTER_PASSWORD_STRENGTH_REGISTER)
                if (it.isEmpty()) subtextView.isVisible = false

            }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.faucet_register_password_repeat)
            field {
                inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                typeface = typefaceMonoRegular
                isSingleLine = false
                doAfterTextChanged { viewModel.changePasswordRepeatField(it.toStringOrEmpty()) }
                viewModel.isAccountPasswordRepeatFieldError.observe(viewLifecycleOwner) { isError = it }
                viewModel.accountPasswordRepeatFieldNoticed.observe(viewLifecycleOwner) { fieldtext = it.toStringOrEmpty() }
            }
            combineNonNull(viewModel.accountPasswordField, viewModel.accountPasswordRepeatField).observe(viewLifecycleOwner) { (field1, field2) ->
                subtextView.textWithVisibility = createStringFilterHint(field1, field2, StringFilter.FILTER_REQUIRE_EQUALS)
                if (field2.isEmpty()) subtextView.isVisible = false
            }
        }
    }
    button {
        text = context.getString(R.string.faucet_register_random_button)
        doOnClick { viewModel.generateRandom() }
    }
    button {
        text = context.getString(R.string.faucet_register_next_button)
        doOnClick { if (viewModel.validate()) showFaucetRegisterDialog(viewModel.generateRegisterInfo()) }
    }
    button {
        text = context.getString(R.string.button_dismiss)
        doOnClick { dismiss() }
    }
    doOnDismiss { viewModel.clear() }
    showSoftKeyboard()
}


