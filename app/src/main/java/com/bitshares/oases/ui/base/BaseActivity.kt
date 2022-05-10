package com.bitshares.oases.ui.base

import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.bitshares.oases.R
import com.bitshares.oases.localPreferenceManager
import com.bitshares.oases.preference.old.createLocalContext
import modulon.extensions.compat.isNightModeOn
import modulon.extensions.view.backgroundTintColor
import modulon.extensions.view.ensuredViewId
import modulon.union.UnionActivity
import modulon.union.toUnion

abstract class BaseActivity : UnionActivity() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_exit_hold)
//        }

        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = if (isNightModeOn) {
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            } else {
                decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            statusBarColor = context.getColor(R.color.transparent)
            navigationBarColor = context.getColor(R.color.transparent)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        }
        actionBar?.hide()
        setTaskDescription(ActivityManager.TaskDescription(null, null, getColor(R.color.background)))
        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.createLocalContext(newBase.toUnion().localPreferenceManager.LANGUAGE.value.locale))
    }

//    override fun finish() {
//        super.finish()
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            overridePendingTransition(R.anim.activity_exit_hold, R.anim.activity_slide_out)
//        }
//    }

    // TODO: 2022/5/10 remove
    final override fun onBackPressed() {
        backPressedCallback.invoke()
    }

    private var backPressedCallback: () -> Boolean = { finish(); true }

    fun BaseActivity.doOnBackPressed(block: () -> Boolean) {
        backPressedCallback = block
    }

    inline fun <reified F : ContainerFragment> FragmentContainerView.addFragment(): F {
        val fragment = F::class.java.newInstance()
        supportFragmentManager.beginTransaction().add(ensuredViewId, fragment).commitNow()
        return fragment
    }

    fun FragmentContainerView.addFragment(clazz: Class<out Fragment>): Fragment {
        val fragment = clazz.newInstance()
        supportFragmentManager.beginTransaction().add(ensuredViewId, fragment).commitNow()
        return fragment
    }

    fun FragmentContainerView.addFragment(fragment: Fragment): Fragment {
        supportFragmentManager.beginTransaction().add(ensuredViewId, fragment).commitNow()
        return fragment
    }

}
