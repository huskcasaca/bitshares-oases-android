package com.bitshares.oases.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bitshares.oases.R
import modulon.extensions.animation.extendedLayoutTransition
import modulon.extensions.compat.activity
import modulon.extensions.view.*
import modulon.layout.actionbar.ActionBarLayout
import modulon.layout.actionbar.actionMenu
import modulon.layout.coordinator.CoordinatorLayout
import modulon.layout.coordinator.behavior.ActionBarBehavior
import modulon.layout.coordinator.behavior.ContainerScrollingBehavior
import modulon.layout.frame.FrameLayout
import modulon.layout.linear.VerticalLayout
import modulon.layout.recycler.RecyclerLayout

abstract class ContainerFragment : BaseFragment() {

    private val baseLayout: CoordinatorLayout by lazyView {
        backgroundTintColor = context.getColor(R.color.background)
    }

    private val actionLayout: ActionBarLayout by lazyView {
        baseLayout.addDefaultRow(this)
        layoutParams = androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply { behavior = ActionBarBehavior(true) }
        actionMenu {
            icon = if (activity.intent.data != null && activity.intent.action == Intent.ACTION_VIEW) R.drawable.ic_cell_cross.contextDrawable() else R.drawable.ic_cell_back_arrow.contextDrawable()
        }
    }

    private val containerLayout: FrameLayout by lazyView {
        baseLayout.addDefaultFill(this)
        layoutParams = androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply { behavior = ContainerScrollingBehavior() }
    }

    private val coordinatorLayout: CoordinatorLayout by lazyView {
        containerLayout.addDefaultFill(this)
    }

    private val linearLayout: VerticalLayout by lazyView {
        containerLayout.addDefaultFill(this)
    }

    private val recyclerLayout: RecyclerLayout by lazyView {
        containerLayout.layoutTransition = extendedLayoutTransition
        containerLayout.addDefaultFill(this)
        containerLayout.shader(Gravity.TOP) {
            isVisible = false
            // TODO: 2022/2/16 replace with behavior
            this@lazyView.doOnScrollEvent { _, _ ->
                isVisible = !this@lazyView.isOnTop
            }
        }
    }

    final override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View {
        return baseLayout
    }

    fun ContainerFragment.setupAction(block: ActionBarLayout.() -> Unit) = actionLayout.block()
    fun ContainerFragment.setupVertical(block: VerticalLayout.() -> Unit) = linearLayout.block()
    fun ContainerFragment.setupCoordinator(block: CoordinatorLayout.() -> Unit) = coordinatorLayout.block()
    fun ContainerFragment.setupRecycler(block: RecyclerLayout.() -> Unit) = recyclerLayout.block()

}

