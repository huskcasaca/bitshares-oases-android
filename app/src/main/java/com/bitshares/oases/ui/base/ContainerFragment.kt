package com.bitshares.oases.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitshares.oases.R
import com.bitshares.oases.ui.asset.browser.actionBarLayout
import com.bitshares.oases.ui.asset.browser.bodyCoordinatorParams
import com.bitshares.oases.ui.asset.browser.actionCoordinatorParams
import modulon.extensions.compat.activity
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.actionbar.ActionBarLayout
import modulon.layout.actionbar.actionMenu
import modulon.layout.coordinator.CoordinatorLayout
import modulon.layout.coordinator.behavior.ActionBarBehavior
import modulon.layout.frame.FrameLayout
import modulon.layout.linear.VerticalLayout
import modulon.layout.recycler.RecyclerLayout

abstract class ContainerFragment : BaseFragment() {

    val baseLayout: CoordinatorLayout by lazyView {
        noClipping()
        backgroundTintColor = context.getColor(R.color.background)
    }

    val actionLayout: ActionBarLayout by lazyView {
        layoutParams = actionCoordinatorParams()
        actionMenu {
            icon = if (activity.intent.data != null && activity.intent.action == Intent.ACTION_VIEW) R.drawable.ic_cell_cross.contextDrawable() else R.drawable.ic_cell_back_arrow.contextDrawable()
        }
        baseLayout.addView(this)
    }

    val containerLayout: FrameLayout by lazyView {
        layoutParams = bodyCoordinatorParams()
        baseLayout.addView(this)
    }

    val coordinatorLayout: CoordinatorLayout by lazyView {
        layoutWidth = MATCH_PARENT
        layoutHeight = MATCH_PARENT
        containerLayout.addView(this)
    }

    val linearLayout: VerticalLayout by lazyView {
        layoutWidth = MATCH_PARENT
        layoutHeight = MATCH_PARENT
        containerLayout.addView(this)
    }

    val recyclerLayout: RecyclerLayout by lazyView {
        noClipping()
//        containerLayout.layoutTransition = extendedLayoutTransition
        layoutWidth = MATCH_PARENT
        layoutHeight = MATCH_PARENT
        containerLayout.addView(this)
    }



    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        onCreateView()
        runCatching {
            CoordinatorLayout(context).apply {
                backgroundTintColor = context.getColor(R.color.background)
                onCreateView()
            }
        }.onSuccess {
            return it
        }

        return baseLayout
    }

    open fun onCreateView() { }

    open fun ViewGroup.onCreateView() {
        TODO()
        actionBarLayout {
            layoutParams = actionCoordinatorParams()
        }
        verticalLayout {
            layoutParams = bodyCoordinatorParams()
            recyclerLayout {
            }
        }
    }

    inline fun ContainerFragment.setupAction(block: ActionBarLayout.() -> Unit) = actionLayout.block()
    inline fun ContainerFragment.setupVertical(block: VerticalLayout.() -> Unit) = linearLayout.block()
    inline fun ContainerFragment.setupCoordinator(block: CoordinatorLayout.() -> Unit) = coordinatorLayout.block()
    inline fun ContainerFragment.setupRecycler(block: RecyclerLayout.() -> Unit) = recyclerLayout.block()



}

