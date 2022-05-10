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
import modulon.component.appbar.AppbarView
import modulon.component.appbar.actionMenu
import modulon.layout.coordinator.CoordinatorView
import modulon.layout.stack.StackView
import modulon.layout.linear.VerticalView
import modulon.layout.lazy.LazyListView

abstract class ContainerFragment : BaseFragment() {

    val baseLayout: CoordinatorView by lazyView {
        noClipping()
        backgroundTintColor = context.getColor(R.color.background)
    }

    val actionLayout: AppbarView by lazyView {
        layoutParams = actionCoordinatorParams()
        actionMenu {
            icon = if (activity.intent.data != null && activity.intent.action == Intent.ACTION_VIEW) R.drawable.ic_cell_cross.contextDrawable() else R.drawable.ic_cell_back_arrow.contextDrawable()
        }
        baseLayout.addView(this)
    }

    val containerLayout: StackView by lazyView {
        layoutParams = bodyCoordinatorParams()
        baseLayout.addView(this)
    }

    val coordinatorLayout: CoordinatorView by lazyView {
        layoutWidth = MATCH_PARENT
        layoutHeight = MATCH_PARENT
        containerLayout.addView(this)
    }

    val linearLayout: VerticalView by lazyView {
        layoutWidth = MATCH_PARENT
        layoutHeight = MATCH_PARENT
        containerLayout.addView(this)
    }

    val recyclerLayout: LazyListView by lazyView {
        noClipping()
//        containerLayout.layoutTransition = extendedLayoutTransition
        layoutWidth = MATCH_PARENT
        layoutHeight = MATCH_PARENT
        containerLayout.addView(this)
    }



    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        onCreateView()
        runCatching {
            CoordinatorView(context).apply {
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

    inline fun ContainerFragment.setupAction(block: AppbarView.() -> Unit) = actionLayout.block()
    inline fun ContainerFragment.setupVertical(block: VerticalView.() -> Unit) = linearLayout.block()
    inline fun ContainerFragment.setupCoordinator(block: CoordinatorView.() -> Unit) = coordinatorLayout.block()
    inline fun ContainerFragment.setupRecycler(block: LazyListView.() -> Unit) = recyclerLayout.block()



}

