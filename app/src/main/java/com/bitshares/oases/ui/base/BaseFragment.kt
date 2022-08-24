package com.bitshares.oases.ui.base

import android.animation.ObjectAnimator
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.lifecycle.*
import com.bitshares.oases.R
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.netowrk.java_websocket.WebSocketState
import com.bitshares.oases.preference.DarkMode
import com.bitshares.oases.ui.settings.network.NetworkSettingsFragment
import com.bitshares.oases.ui.wallet.WalletSettingsFragment
import com.bitshares.oases.ui.wallet.startWalletUnlock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modulon.extensions.animation.doOnAnimationEnd
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.ensuredViewId
import modulon.extensions.view.parentViewGroup
import modulon.component.appbar.AppbarView
import modulon.component.appbar.menu
import modulon.union.UnionFragment

abstract class BaseFragment : UnionFragment() {

    // TODO: 2022/2/8 used to prevent lifecycle issues
    //  remove in future
    private var isViewCreated = false

    // TODO: 7/12/2021 apply to all fragments
    open fun onViewCreated(savedInstanceState: Bundle?) {
        /* no-op */
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isViewCreated) return
        isViewCreated = true
        onViewCreated(savedInstanceState)
    }

    // TODO: 2022/2/19 move to extensions


    fun AppbarView.titleConnectionState(text: CharSequence) {
        title = text
        NetworkService.connectionState.map {
            when (it) {
                WebSocketState.CONNECTING -> context.getString(R.string.connection_state_connecting)
                WebSocketState.CONNECTED, WebSocketState.MESSAGING -> text
                WebSocketState.CLOSED -> context.getString(R.string.connection_state_no_connection)
            }
        }.distinctUntilChanged().observe { title = it }
    }

    fun AppbarView.websocketStateMenuInternal() = menu {
        text = "Network State"
        isVisible = false
        globalPreferenceManager.INDICATOR.observe(viewLifecycleOwner) { isVisible = it }
        val connected = R.drawable.ic_menu_network_connected.contextDrawable() as AnimatedVectorDrawable
        val closed = R.drawable.ic_menu_network_closed.contextDrawable() as AnimatedVectorDrawable
        val connecting = R.drawable.ic_menu_network_connecting.contextDrawable() as AnimatedVectorDrawable
        fun startAnimation() {
            CoroutineScope(Dispatchers.Main).launch { (icon as AnimatedVectorDrawable).start() }
        }
        if (icon == connecting) startAnimation()
        connecting.doOnAnimationEnd { startAnimation() }
        val oa = ObjectAnimator.ofFloat(iconView, "rotation", 0f, 360f).apply {
            duration = 1000
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
        }
//        globalWebsocketManager.state.asLiveData().observe(viewLifecycleOwner) {
//            when (it) {
//                GrapheneClient.State.Connecting -> run {
//                    if (icon != connecting) {
//                        icon = connecting
//                        oa.start()
//                    }
//                }
//                GrapheneClient.State.Connected -> run {
//                    if (icon != connected) {
//                        connecting.reset()
//                        connected.reset()
//                        closed.reset()
//                        icon = connected
//                        startAnimation()
//                        oa.cancel()
//                        iconView.rotation = 0f
//                    }
//                }
//                GrapheneClient.State.Closed -> run {
//                    if (icon != closed) {
//                        connecting.reset()
//                        connected.reset()
//                        closed.reset()
//                        icon = closed
//                        startAnimation()
//                        oa.cancel()
//                        iconView.rotation = 0f
//                    }
//                }
//            }
//        }
        doOnClick { startFragment<NetworkSettingsFragment>() }
        doOnLongClick { startFragment<NetworkSettingsFragment>() }
    }

    fun AppbarView.walletStateMenu() = menu {
        text = "Network State"
        isVisible = false
        globalPreferenceManager.INDICATOR.observe(viewLifecycleOwner) { isVisible = it }
        val locked = R.drawable.ic_menu_wallet_locked.contextDrawable() as AnimatedVectorDrawable
        val unlocked = R.drawable.ic_menu_wallet_unlocked.contextDrawable() as AnimatedVectorDrawable
        icon = if (globalWalletManager.isUnlocked.value) unlocked else locked
        locked.doOnAnimationEnd {
            icon = unlocked
            locked.reset()
            if (!globalWalletManager.isUnlocked.value) unlocked.start()
        }
        unlocked.doOnAnimationEnd {
            icon = locked
            unlocked.reset()
            if (globalWalletManager.isUnlocked.value) locked.start()
        }
        globalWalletManager.isUnlocked.observe { isUnlocked ->
            if (!(icon as AnimatedVectorDrawable).isRunning && (isUnlocked != (icon == unlocked))) {
                icon = if (isUnlocked) locked else unlocked
                (icon as AnimatedVectorDrawable).start()
            }
        }
        doOnClick { if (globalWalletManager.isUnlocked.value) globalWalletManager.lock() else lifecycleScope.launch(Dispatchers.Main.immediate) { startWalletUnlock() } }
        doOnLongClick { startFragment<WalletSettingsFragment>() }
    } to menu {
        icon = R.drawable.ic_cell_appearance.contextDrawable()
        doOnClick {
            globalPreferenceManager.DARK_MODE.value = when(globalPreferenceManager.DARK_MODE.value) {
                DarkMode.FOLLOW_SYSTEM -> DarkMode.ON
                DarkMode.OFF -> DarkMode.ON
                DarkMode.ON -> DarkMode.OFF
                DarkMode.AUTO_BATTERY -> DarkMode.ON
            }
        }

    }

    fun AppbarView.websocketStateMenu() {
        websocketStateMenuInternal()
    }

    fun AppbarView.broadcastMenu(block: AppbarView.Item.() -> Unit) = menu {
        text = "Transaction Broadcast"
        icon = R.drawable.ic_test_outline_done_24.contextDrawable()
        isClickable = false
        isVisible = false
        apply(block)
    }

    fun BaseFragment.doOnBackPressed(block: () -> Boolean) {
        (activity as BaseActivity).apply {
            doOnBackPressed(block)
        }
    }

    fun FragmentContainerView.setFragment(fragment: BaseFragment) {
        childFragmentManager.commit {
            add(ensuredViewId, fragment)
        }
    }

    inline fun <reified F : BaseFragment> FragmentContainerView.setFragment() {
        childFragmentManager.commit {
            add(ensuredViewId, F::class.java.newInstance())
        }
    }

    inline fun <reified F : BaseFragment> BaseFragment.addThis() {
        parentFragmentManager.commit {
//            setCustomAnimations(R.anim.activity_slide_in, R.anim.activity_exit_hold, R.anim.activity_exit_hold, R.anim.activity_exit_hold)
            replace(containerId, F::class.java.newInstance())
            addToBackStack(null)
        }
    }

    inline fun <reified F : BaseFragment> F.detachSelf() {
        parentFragmentManager.commit {
//            setCustomAnimations(R.anim.activity_exit_hold, R.anim.activity_slide_out, R.anim.activity_exit_hold, R.anim.activity_slide_out)
            remove(this@detachSelf)
        }
        if (parentFragmentManager.backStackEntryCount == 0) activity?.finish() else parentFragmentManager.popBackStack()
    }

    val Fragment.containerId: Int get() = view?.parentViewGroup?.id ?: 0

}
