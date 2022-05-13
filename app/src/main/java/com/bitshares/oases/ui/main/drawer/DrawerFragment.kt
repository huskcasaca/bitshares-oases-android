package com.bitshares.oases.ui.main.drawer

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.net.toUri
import androidx.core.view.doOnAttach
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.database.entities.toUser
import com.bitshares.oases.extensions.compat.*
import com.bitshares.oases.extensions.viewbinder.bindKdenticonAvatar
import com.bitshares.oases.extensions.viewbinder.bindUserDrawer
import com.bitshares.oases.extensions.viewbinder.bindUserV1
import com.bitshares.oases.extensions.viewbinder.setDrawerItemStyle
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.ui.about.AboutFragment
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.account.importer.ImportFragment
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startFragment
import com.bitshares.oases.ui.intro.IntroFragment
import com.bitshares.oases.ui.main.MainViewModel
import com.bitshares.oases.ui.testlab.TestLabFragment
import com.bitshares.oases.ui.wallet.showUserOptionDialog
import com.bitshares.oases.ui.wallet.showUserSwitchDialog
import modulon.component.cell.BaseCell
import modulon.component.cell.ComponentPaddingCell
import modulon.component.cell.IconSize
import modulon.extensions.animation.doOnAnimationEnd
import modulon.extensions.compat.startUriBrowser
import modulon.extensions.font.typefaceBold
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.graphics.createRoundRectDrawable
import modulon.extensions.livedata.combineFirst
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.linear.VerticalView
import modulon.layout.lazy.construct

class DrawerFragment : ContainerFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()

    private class Divider(context: Context) : BaseCell(context) {

        private val divider = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.component_separator)
            strokeWidth = 1.5.dpf
        }

        override fun onDraw(canvas: Canvas) {
            divider.color = context.getColor(R.color.component_separator)
            canvas.drawLine(0f, measuredHeight / 2f, measuredWidth - 0f, measuredHeight / 2f, divider)
            super.onDraw(canvas)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(1.5.dp, MeasureSpec.EXACTLY)
            )
            setMeasuredDimension(measuredWidth, height)
        }
    }

    override fun onCreateView() {
        setupRecycler {
            noPadding()
            verticalLayout {
                view<ComponentPaddingCell> {
                    background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(0x14000000, 0x00000000))
                    doOnAttach {
                        updatePadding(top = 16.dp + (rootWindowInsets?.systemWindowInsetTop ?: 0))
                    }
                    iconSize = IconSize.SIZE_9
                    val expander = R.drawable.ic_arrow_closed.contextDrawable() as AnimatedVectorDrawable
                    val closer = R.drawable.ic_arrow_expanded.contextDrawable() as AnimatedVectorDrawable
                    val imageView = ImageView(context).apply {
                        expander.mutate().setTint(context.getColor(R.color.cell_text_primary))
                        expander.doOnAnimationEnd {
                            background = closer
                            expander.reset()
                        }
                        closer.mutate().setTint(context.getColor(R.color.cell_text_primary))
                        closer.doOnAnimationEnd {
                            background = expander
                            closer.reset()
                        }
                        setBackgroundDrawable(expander)
                    }
                    setPadding(24.dp, 16.dp, 24.dp, 16.dp)
                    title = context.getString(R.string.import_no_account_hint)
                    subtext = context.getString(R.string.import_no_id_hint)
                    verticalLayout {
                        view(iconView) {
                            val backgroundColor = context.getColor(R.color.drawer_background_avatar)
                            val backgroundRadius = context.resources.getDimension(iconSize.size) / 10
                            background = createRoundRectDrawable(backgroundColor, backgroundRadius)
                            layoutWidth = context.resources.getDimensionPixelSize(iconSize.size)
                            layoutHeight = context.resources.getDimensionPixelSize(iconSize.size)
                        }
                        viewRow(titleView) {
                            setTextColor(context.getColor(R.color.cell_text_primary))
                            textSize = 16f
                            typeface = typefaceBold
                            isAllCaps = true
                            startScrolling()
                            updatePadding(top = context.resources.getDimensionPixelSize(R.dimen.navigation_drawer_top_offset))
                        }
                        viewRow(subtextView) {
                            setTextColor(context.getColor(R.color.cell_text_secondary))
                            textSize = 14f
                            isAllCaps = true
                            text = context.getString(R.string.import_no_id_hint)
                        }
                    }
                    view(imageView) {
                        layoutGravityFrame = Gravity.BOTTOM or Gravity.END
                    }
                    background = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(R.color.background_dark.contextColor(), R.color.background_component.contextColor()))
                    fun setExpanded(value: Boolean) {
                        if (closer.isRunning || expander.isRunning) {
                            closer.reset()
                            expander.reset()
                            imageView.background = if (value) closer else expander
                        }
                        if (value) expander.start() else closer.start()
                    }

                    val fadeOut = ObjectAnimator.ofFloat(iconView, "alpha", 1f, 0f).apply {
                        duration = 240
                        interpolator = AccelerateInterpolator()
                        doOnEnd {
                            if (Settings.KEY_CURRENT_ACCOUNT_ID.isDefault()) {
                                bindKdenticonAvatar(System.currentTimeMillis().toString(), iconSize)
                            }
                        }
                    }
                    val fadeIn = ObjectAnimator.ofFloat(iconView, "alpha", 0f, 1f).apply {
                        duration = 240
                        interpolator = AccelerateInterpolator()
                    }
                    val animation = AnimatorSet().apply {
                        play(fadeOut).after(4800).after(fadeIn)
                        doOnEnd { if (Settings.KEY_CURRENT_ACCOUNT_ID.isDefault()) start() }
                    }
                    combineFirst(mainViewModel.userCurrent, accountViewModel.accountStaticById).observe(viewLifecycleOwner) { (user, account) ->
                        if (user == null) {
                            iconView.doOnClick {
                                startImport()
                                mainViewModel.closeDrawer()
                            }
                            bindKdenticonAvatar(System.currentTimeMillis().toString(), iconSize)
            //                                subtextView.isMonospaced = false
                            title = context.getString(R.string.import_no_account_hint).toUpperCase()
                            subtextView.typeface = subtextView.typefaceBold
                            subtext = context.getString(R.string.import_no_id_hint)
                            if (Settings.KEY_CURRENT_ACCOUNT_ID.isDefault()) animation.start()
                        } else {
                            animation.cancel()
                            iconView.alpha = 1f
                            subtextView.typeface = typefaceMonoRegular
                            iconView.doOnClick {
                                startAccountBrowser(user.uid)
                                mainViewModel.closeDrawer()
                            }
                            // FIXME: 2021/1/6
                            if (account != null) {
                                bindUserV1(account.toUser(), IconSize.SIZE_7)
                            } else {
                                bindUserV1(user, IconSize.SIZE_7)
                            }
                        }
                    }
                    mainViewModel.isUsersExpanded.observe(viewLifecycleOwner) {
                        setExpanded(it)
                    }
                    doOnClick { mainViewModel.changeExpandState() }
                }
                spacer {
                    height = 3
                    val divider = Paint().apply {
                        color = context.getColor(R.color.component_separator)
                        strokeWidth = 3f
                    }
                    doOnDraw { it.drawLine(0f, measuredHeight / 2f, measuredWidth - 0f, measuredHeight / 2f, divider) }
                }
            }
            // expandable
            verticalLayout {
                spacer { height = 4.dp }
                verticalLayout {
                    mainViewModel.users.observe(viewLifecycleOwner) { list ->
                        removeAllViews()
                        list.forEach { user ->
                            frameLayout {
                                updatePadding(right = 16.dp)
                                cell {
                                    setDrawerItemStyle()
                                    bindUserDrawer(user, IconSize.SMALL)
                                    iconView.apply {
                                        layoutMarginStart = (-4).dp
                                        layoutMarginTop = (-4).dp
                                        layoutMarginEnd = (-4).dp + componentOffset
                                        layoutMarginBottom = (-4).dp
                                        layoutGravity = Gravity.START or Gravity.CENTER_VERTICAL
                                    }
//                                    radRT: Float, radLB: Float, radRB: Float, radLT: Float
                                    doOnClick {
                                        mainViewModel.closeDrawer()
                                        showUserSwitchDialog(user)
                                    }
                                    doOnLongClick { showUserOptionDialog(user) }
                                }
                            }
                        }
                    }
                }
                // TODO: 2022/2/24 remove frameLayout
                frameLayout {
                    updatePadding(right = 16.dp)
                    cell {
                        setDrawerItemStyle()
                        title = context.getString(R.string.drawer_import)
                        icon = R.drawable.ic_cell_add_account.contextDrawable()
                        doOnClick {
                            startFragment<ImportFragment>()
                            mainViewModel.closeDrawer()
                        }
                    }
                }
                spacer { height = 4.dp }
                spacer {
                    height = 3
                    val divider = Paint().apply {
                        color = context.getColor(R.color.component_separator)
                        strokeWidth = 3f
                    }
                    doOnDraw { it.drawLine(0f, measuredHeight / 2f, measuredWidth - 0f, measuredHeight / 2f, divider) }
                }

                isVisible = false
                mainViewModel.isUsersExpanded.observe(viewLifecycleOwner) { isVisible = it }
            }
            verticalLayout {
                verticalLayout {
                    spacer { height = 4.dp }
                    frameLayout {
                        updatePadding(right = 16.dp)
                        cell {
                            setDrawerItemStyle()
                            title = context.getString(R.string.drawer_transfer)
                            icon = R.drawable.ic_cell_transfer.contextDrawable()
                            mainViewModel.isUserAvailable.observe(viewLifecycleOwner) {
                                doOnThrottledClick {
                                    if (it) startTransferFrom(Settings.KEY_CURRENT_ACCOUNT_ID.value) else startImport()
                                    mainViewModel.closeDrawer()
                                }
                            }
                        }
                    }
                    frameLayout {
                        updatePadding(right = 16.dp)
                        cell {
                            setDrawerItemStyle()
                            title = context.getString(R.string.drawer_collateral)
                            icon = R.drawable.ic_cell_collateral.contextDrawable()
                            mainViewModel.isUserAvailable.observe(viewLifecycleOwner) {
                                doOnThrottledClick {
                                    if (it) startMarginPosition(Settings.KEY_CURRENT_ACCOUNT_ID.value) else startImport()
                                    mainViewModel.closeDrawer()
                                }
                            }
                        }
                    }
                    frameLayout {
                        updatePadding(right = 16.dp)
                        cell {
                            setDrawerItemStyle()
                            title = context.getString(R.string.drawer_voting)
                            icon = R.drawable.ic_cell_voting.contextDrawable()
                            mainViewModel.isUserAvailable.observe(viewLifecycleOwner) {
                                doOnThrottledClick {
                                    if (it) startVoting(Settings.KEY_CURRENT_ACCOUNT_ID.value) else startImport()
                                    mainViewModel.closeDrawer()
                                }
                            }
                        }
                    }
                    frameLayout {
                        updatePadding(right = 16.dp)
                        cell {
                            setDrawerItemStyle()
                            title = context.getString(R.string.drawer_proposed_transactions)
                            icon = R.drawable.ic_cell_proposal_manage.contextDrawable()
                            mainViewModel.isUserAvailable.observe(viewLifecycleOwner) {
                                doOnThrottledClick {
                                    startFragment<IntroFragment>()
                                    mainViewModel.closeDrawer()
                                }
                            }
                        }
                    }
                    spacer { height = 4.dp }
                    view<Divider>()
                }
                verticalLayout {
                    spacer { height = 4.dp }
                    frameLayout {
                        updatePadding(right = 16.dp)
                        cell {
                            setDrawerItemStyle()
                            title = context.getString(R.string.drawer_membership)
                            icon = R.drawable.ic_cell_membership.contextDrawable()
                            doOnThrottledClick {
                                startMembership(Settings.KEY_CURRENT_ACCOUNT_ID.value)
                                mainViewModel.closeDrawer()
                            }
                        }
                    }
                    spacer { height = 4.dp }
                    view<Divider>()
                }
                verticalLayout {
                    spacer { height = 4.dp }
                    frameLayout {
                        updatePadding(right = 16.dp)
                        cell {
                            setDrawerItemStyle()
                            title = context.getString(R.string.drawer_settings)
                            icon = R.drawable.ic_cell_settings.contextDrawable()
                            doOnThrottledClick {
                                startSettings()
                                mainViewModel.closeDrawer()
                            }
                        }
                    }
                    frameLayout {
                        updatePadding(right = 16.dp)
                        cell {
                            setDrawerItemStyle()
                            title = context.getString(R.string.drawer_help)
                            icon = R.drawable.ic_cell_help.contextDrawable()
                            doOnThrottledClick {
                                startUriBrowser(AppConfig.HELP_URL.toUri())
                                mainViewModel.closeDrawer()
                            }
                        }
                    }
                    frameLayout {
                        updatePadding(right = 16.dp)
                        cell {
                            setDrawerItemStyle()
                            title = context.getString(R.string.drawer_about)
                            icon = R.drawable.ic_cell_about.contextDrawable()
                            doOnThrottledClick {
                                startFragment<AboutFragment>()
                                mainViewModel.closeDrawer()
                            }
                        }
                    }
                    frameLayout {
                        updatePadding(right = 16.dp)
                        cell {
                            setDrawerItemStyle()
                            title = context.getString(R.string.drawer_test_lab)
                            icon = R.drawable.ic_cell_test_lab.contextDrawable()
                            doOnThrottledClick {
                                startFragment<TestLabFragment>()
                                mainViewModel.closeDrawer()
                            }
                        }
                    }
                    spacer { height = 4.dp }
                }
                foreground = ColorDrawable(0x00ffffff)
                backgroundTintColor = context.getColor(R.color.drawer_background)
                val backgroundColor = context.getColor(R.color.drawer_background)
                val colorAnimatorIn = ValueAnimator.ofArgb(backgroundColor and 0x00FFFFFF, backgroundColor and 0xCCFFFFFF.toInt()).apply {
                    duration = 240L
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener { (foreground as ColorDrawable).color = it.animatedValue as Int }
                }
                val colorAnimatorOut = ValueAnimator.ofArgb(backgroundColor and 0xCCFFFFFF.toInt(), backgroundColor and 0x00FFFFFF).apply {
                    duration = 240L
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener {
                        (foreground as ColorDrawable).color = it.animatedValue as Int
                    }
                }
                mainViewModel.isUsersExpanded.observe(viewLifecycleOwner) {
                    if (it) colorAnimatorIn.start() else colorAnimatorOut.start()
                }
            }
            backgroundTintColor = context.getColor(R.color.drawer_background)
            parentView.backgroundTintColor = context.getColor(R.color.drawer_background)
        }
    }
}

