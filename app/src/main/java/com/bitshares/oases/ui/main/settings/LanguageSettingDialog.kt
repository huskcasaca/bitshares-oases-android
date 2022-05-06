package com.bitshares.oases.ui.main.settings

import android.view.Gravity
import android.view.ViewGroup
import com.bitshares.oases.R
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.preference.old.I18N
import com.bitshares.oases.ui.settings.SettingsViewModel
import modulon.dialog.ExpandableFragment
import modulon.dialog.button
import modulon.extensions.compat.recreateActivity
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.lifecycle.parentViewModels
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.recyclerLayout
import modulon.layout.recycler.section
import modulon.union.Union
import modulon.widget.RadioView

class LanguageSettingDialog : ExpandableFragment() {

    val viewModel: SettingsViewModel by parentViewModels()

    override fun ViewGroup.onCreateView() {
        recyclerLayout {
            section {
                I18N.values().forEach { locale ->
                    cell {
                        customViewStart = create<RadioView> {
                            setColors(context.getColor(R.color.component_disabled), context.getColor(R.color.component))
                            layoutWidth =  context.resources.getDimensionPixelSize(modulon.R.dimen.icon_size_tiny)
                            layoutHeight = context.resources.getDimensionPixelSize(modulon.R.dimen.icon_size_tiny)
                            layoutGravityFrame = Gravity.START or Gravity.CENTER_VERTICAL
                            viewModel.language.observe(viewLifecycleOwner) {
                                setChecked(it == locale, true)
                            }
                        }
                        updatePaddingVerticalHalf()
                        text = when (locale) {
                            I18N.DEFAULT                -> context.getString(R.string.language_settings_default)
                            else                        -> locale.localizedName
                        }
                        subtext = when (locale) {
                            I18N.DEFAULT                -> context.getString(R.string.language_settings_follow_system)
                            I18N.ENGLISH                -> context.getString(R.string.language_settings_english)
                            I18N.RUSSIAN                -> context.getString(R.string.language_settings_russian)
                            I18N.SIMPLIFIED_CHINESE     -> context.getString(R.string.language_settings_simplified_chinese)
                            I18N.TRADITIONAL_CHINESE    -> context.getString(R.string.language_settings_traditional_chinese)
                        }
                        doOnClick {
                            globalPreferenceManager.LANGUAGE.value = locale
                            detachSelf()
                            recreateActivity()
                        }
                    }
                }
            }
        }

    }


}

fun Union.showLanguageSettingDialog() = showBottomDialog {
    title = context.getString(R.string.language_settings_title)
    isCancelableByButtons = true

    button { text = context.getString(R.string.button_cancel) }
}