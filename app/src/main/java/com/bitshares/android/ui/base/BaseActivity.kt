package com.bitshares.android.ui.base

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.bitshares.android.R
import com.bitshares.android.preference.old.I18N
import com.bitshares.android.preference.old.LocaleService
import com.bitshares.android.preference.old.Settings
import modulon.extensions.compat.isNightModeOn
import modulon.extensions.view.backgroundTintColor
import modulon.extensions.view.ensureViewId
import modulon.union.UnionActivity

abstract class BaseActivity : UnionActivity() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_exit_hold)
        }
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = if (isNightModeOn) {
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            } else {
                decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            statusBarColor = context.getColor(R.color.transparent)
            navigationBarColor = context.getColor(R.color.background)
            decorView.backgroundTintColor = context.getColor(R.color.background)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isNavigationBarContrastEnforced = true
            }
        }
        actionBar?.hide()
        setTaskDescription(ActivityManager.TaskDescription(null, null, getColor(R.color.background)))
        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context) {
        val locale = I18N.values().getOrNull(Settings.KEY_LANGUAGE.value)?.locale
        if (locale == null) {
            if (!Settings.KEY_LANGUAGE.isDefault()) Settings.KEY_LANGUAGE.reset()
            super.attachBaseContext(newBase)
        } else {
            super.attachBaseContext(LocaleService.updateLocale(newBase, locale))
        }
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            overridePendingTransition(R.anim.activity_exit_hold, R.anim.activity_slide_out)
        }
    }

    final override fun onBackPressed() {
        backPressedCallback.invoke()
    }

    private var backPressedCallback: () -> Boolean = { finish(); true }

    fun BaseActivity.doOnBackPressed(block: () -> Boolean) {
        backPressedCallback = block
    }

    inline fun <reified F : ContainerFragment> FragmentContainerView.addFragment(): F {
        val fragment = F::class.java.newInstance()
        supportFragmentManager.beginTransaction().add(ensureViewId(), fragment).commitNow()
        return fragment
    }

    fun FragmentContainerView.addFragment(clazz: Class<out Fragment>): Fragment {
        val fragment = clazz.newInstance()
        supportFragmentManager.beginTransaction().add(ensureViewId(), fragment).commitNow()
        return fragment
    }

    fun FragmentContainerView.addFragment(fragment: Fragment): Fragment {
        supportFragmentManager.beginTransaction().add(ensureViewId(), fragment).commitNow()
        return fragment
    }

}
