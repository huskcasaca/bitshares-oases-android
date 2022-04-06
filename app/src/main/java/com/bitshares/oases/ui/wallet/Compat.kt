package com.bitshares.oases.ui.wallet

import android.content.Context
import android.text.InputFilter
import android.view.Gravity
import android.view.KeyEvent.*
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.*
import androidx.core.widget.doAfterTextChanged
import com.bitshares.oases.R
import com.bitshares.oases.globalWalletManager
import kotlinx.coroutines.*
import modulon.component.ComponentCell
import modulon.component.ComponentSpacerCell
import modulon.dialog.*
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.compat.showBooleanSuspendedBottomDialog
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.font.typefaceRegular
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.temp.drawShaders
import modulon.extensions.text.PatternInputFilter
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.linear.HorizontalLayout
import modulon.union.Union
import modulon.widget.FieldTextView
import graphene.extension.*

private class DigitPasswordLayout(context: Context) : HorizontalLayout(context) {

    private fun ViewGroup.fieldSpacer() = view<ComponentSpacerCell> {
        layoutWidth = context.resources.getDimensionPixelSize(R.dimen.global_spacer_size)
        layoutHeight = 0
    }

    val passwordFields get() =
        children.filter { it is ComponentCell }.map { (it as ViewGroup).getChildAt<FieldTextView>(2) }.toList()

    @Deprecated("")
    val passcode get() =
        children.filter { it is ComponentCell }.map { (it as ViewGroup).getChildAt<FieldTextView>(2) }.toList().map { it.fieldtext.toString() }

    val currentPassword = NonNullMutableLiveData("")


    var isFieldEnabled: Boolean = true
        set(value) {
            passwordFields.forEach { it.isEnabled = value }
            field = value
        }

    var isFieldError: Boolean = false
        set(value) {
            if (value != field) {
                passwordFields.forEach {
                    it.parentView.backgroundTintColor = if (!value) R.color.background_component.contextColor() else R.color.drawer_background_avatar_e.contextColor()
                }
                field = value
            }
        }

    fun clear() {
        currentPassword.value = ""
        passwordFields.forEach { it.fieldtext = EMPTY_SPACE }
    }

    init {
        noClipping()
        fieldSpacer()
        repeat(6) { index ->
            cell {
                noClipping()
                view<FieldTextView> {
                    textSize = 28f
                    inputType = EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD or EditorInfo.TYPE_CLASS_NUMBER
                    transformationMethod = null
                    typeface = typefaceRegular
                    gravity = Gravity.CENTER
                    layoutGravity = Gravity.CENTER
                    layoutWidth = MATCH_PARENT
                    maxLines = 1
                    isEditable
                    filters = arrayOf(InputFilter.LengthFilter(1), PatternInputFilter(Regex("[0-9]")))
                    isCursorVisible = false
                    background = null
                    setOnKeyListener { v, keyCode, event ->
                        if (event.action == ACTION_UP) {
                            if (event.keyCode == KEYCODE_ENTER || event.keyCode == KEYCODE_NUMPAD_ENTER) {
                                return@setOnKeyListener false
                            }
                            fun delKey() {
                                currentPassword.value = currentPassword.value.dropLast(1)
                            }
                            fun numKey(key: String) {
                                currentPassword.value = (currentPassword.value + key).take(6)
                            }
                            when (event.keyCode) {
                                KEYCODE_DEL -> delKey()
                                KEYCODE_0   -> numKey("0")
                                KEYCODE_1   -> numKey("1")
                                KEYCODE_2   -> numKey("2")
                                KEYCODE_3   -> numKey("3")
                                KEYCODE_4   -> numKey("4")
                                KEYCODE_5   -> numKey("5")
                                KEYCODE_6   -> numKey("6")
                                KEYCODE_7   -> numKey("7")
                                KEYCODE_8   -> numKey("8")
                                KEYCODE_9   -> numKey("9")
                                else -> Unit
                            }
                        }
                        true
                    }
                    doAfterTextChanged {
                        val num = currentPassword.value.getOrNull(index).toStringOrEmpty()
                        if (fieldtext.toStringOrEmpty() != num) {
                            fieldtext = num
                        }
                        isFieldError = false
                    }
                    currentPassword.observeForever {
                        fieldtext = it.getOrNull(index).toStringOrEmpty()
                        if (it.length.coerceIn(0..5) == index) {
                            requestFocus()
                        }
                    }
                    // FIXME: 2022/3/12
                    if (index == 0) postDelayed(100) { showSoftKeyboard() }
                }
                updatePadding(left = 0.dp, top = 2.dp, right = 0.dp, bottom = 2.dp)
                drawShaders()
                layoutWeightLinear = 1f
            }
            fieldSpacer()
        }
        doOnClick {
            showSoftKeyboard()
        }
    }

}

private fun ViewGroup.passcodeLayout(block: DigitPasswordLayout.() -> Unit = {}) = view(block)


const val PASSWORD_ITERATION = 1_000_000

suspend fun Union.showWalletPasswordUnlockDialog(changePassword: Boolean = false) = showBooleanSuspendedBottomDialog {
    if (globalWalletManager.unlock()) dismissWith(true)
    title = context.getString(R.string.wallet_unlock_wallet_title)
    message = if (changePassword) "Enter your old password" else "Enter your password"
    var unlockJob: Job = Job()
    section {
        passcodeLayout {
            fun unlock(password: String) {
                state = DialogState.PENDING
                title = "Unlocking..."
                isFieldEnabled = false
                isCancelableTouchOutside = false
                unlockJob = lifecycleScope.launch(Dispatchers.IO) {
                    val encoded = coroutineScope {
                        password.sha256(PASSWORD_ITERATION)
                    }.encodeBase64()
                    launch(Dispatchers.Main) {
                        if (globalWalletManager.unlock(encoded)) {
                            state = DialogState.SUCCESS
                            dismissWith(true)
                        } else {
                            isCancelableTouchOutside = true
                            state = DialogState.FAILURE
                            title = "Unlock Wallet"
                            currentPassword.value = ""
                            message = "Incorrect password!"

                            isFieldError = true
                            isFieldEnabled = true
                            passwordFields.first().requestFocus()
                            postDelayed(100) {
                                passwordFields.first().showSoftKeyboard()
                            }
                        }
                    }
                }
            }
            currentPassword.observe(viewLifecycleOwner) {
                state = DialogState.EMPTY
                if (it.length == 6) {
                    unlock(passcode.joinToString(""))
                }
                message = if (changePassword) "Enter your old password" else "Enter your password"
            }
        }
    }
    button {
        text = context.getString(R.string.button_cancel)
        doOnClick { dismissWith(false) }
    }
    doOnDismiss {
        unlockJob.cancel()
        resumeWith(false)
    }
    showSoftKeyboard()
}

suspend fun Union.showWalletChangePasswordDialog() = showBooleanSuspendedBottomDialog {
    title = context.getString(R.string.wallet_change_password_title)
    val isUnderVerifyLayout = NonNullMutableLiveData(false)
    isUnderVerifyLayout.observe(viewLifecycleOwner) {
    }
    var defPasscodeLayout: DigitPasswordLayout = DigitPasswordLayout(context)
    var verPasscodeLayout: DigitPasswordLayout = DigitPasswordLayout(context)

    section {
        passcodeLayout { // password verify
            verPasscodeLayout = this
            isUnderVerifyLayout.observe(viewLifecycleOwner) {
                isVisible = it
                if (it) {
                    postDelayed(100) {
                        passwordFields.first().apply {
                            performClick()
                            requestFocus()
                        }
                    }
                }
            }
        }
        passcodeLayout { // password
            defPasscodeLayout = this
            isUnderVerifyLayout.observe(viewLifecycleOwner) {
                isVisible = !it
                if (!it) {
                    postDelayed(100) {
                        passwordFields.first().apply {
                            performClick()
                            requestFocus()
                        }
                    }
                }
            }
        }
    }
    button {
        isUnderVerifyLayout.observe(viewLifecycleOwner) {
            text = if (it) "Confirm" else "Next"
        }
        combineNonNull(defPasscodeLayout.currentPassword, verPasscodeLayout.currentPassword, isUnderVerifyLayout).observe(viewLifecycleOwner) { (def, ver, layout) ->
            message = when {
                !layout -> "Enter your new password"
                ver.length == 6 && ver != def -> "Passwords not match!"
                else -> "Verify your new password"
            }
            if (layout) {
                isEnabled = def.length == 6 && ver == def
                doOnClick {
                    state = DialogState.PENDING
                    title = "Changing Password..."
                    verPasscodeLayout.isFieldEnabled = false
                    verPasscodeLayout.isFieldError = false
                    updateButton(0) { isEnabled = false; isVisible = false }
                    updateButton(1) { isEnabled = false; isVisible = false }
                    isCancelableTouchOutside = false
                    lifecycleScope.launch(Dispatchers.IO) {
                        val encoded = coroutineScope {
                            verPasscodeLayout.passcode.joinToString("").sha256(PASSWORD_ITERATION)
                        }.encodeBase64()
                        launch(Dispatchers.Main) {
                            state = DialogState.SUCCESS
                            dismissWith(globalWalletManager.changePassword(encoded))
                        }
                    }
                }
                if (ver.length == 6 && ver != def) {
                    verPasscodeLayout.isFieldError = true
                }
            } else {
                isEnabled = def.length == 6
                doOnClick {
                   if (def.length == 6) isUnderVerifyLayout.value = true
                }
            }
        }
    }
    button {
        text = context.getString(R.string.button_cancel)
        doOnClick { dismissWith(false) }
    }
    doOnDismiss { resumeWith(false) }
}
