package com.bitshares.oases.ui.about

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.ui.account.permission.PermissionViewModel
import com.bitshares.oases.ui.base.*
import modulon.component.IconSize
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.compat.startUriBrowser
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.actionbar.subtitle
import modulon.layout.actionbar.title
import modulon.layout.recycler.section

class AboutFragment : ContainerFragment() {

    private val viewModel: PermissionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction {
            title(context.getString(R.string.about_title))
            subtitle(AppConfig.APP_NAME)
        }
        setupRecycler {
//            section {
//                cell {
//                    iconSize = IconSize.LARGE
//                    icon = R.drawable.ic_logo_app.contextDrawable().apply {
//                        mutate()
//                        setTint(context.getColor(R.color.cell_text_primary))
//                    }
//                    subtitleView.typeface = typefaceBold
//                    subtitleView.textSize = 22f
//                    subtextView.textSize = 15f
//                    text = context.getString(R.string.app_name)
//                    subtext = AppConfig.APP_VERSION
//                    ObjectAnimator.ofFloat(iconView, "rotation", 0f, 360f).apply {
//                        duration = 3000
//                        interpolator = LinearInterpolator()
//                        repeatCount = ObjectAnimator.INFINITE
//                        post { start() }
//                    }
//                }
//            }
            section {
                header = "About"
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.about_app_name)
                    subtitle = AppConfig.APP_NAME
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.about_version)
                    subtitle = AppConfig.APP_VERSION
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.about_author)
                    subtitle = AppConfig.AUTHOR
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = "Licenses"
                    doOnClick {
                        showBottomDialog {
                            title = "Licenses"
                        }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.about_privacy_policy)
                    doOnClick {
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
//                    title = context.getString(R.string.about_source_code)
                    title = "Website"
                    subtitle = "Goto"
                    doOnClick {
                        startUriBrowser(AppConfig.PROJECT_SOURCE_CODE_URL.toUri())
                    }
                }
            }
            section {
//                title = context.getString(R.string.about_library)
                header = "Libraries"
                AppConfig.libraries.forEach { library ->
                    cell {
                        updatePaddingVerticalHalf()
                        iconSize = IconSize.SMALL
                        icon = library.icon.contextDrawable()
                        text = library.name
                        subtextView.isSingleLine = true
                        subtext = library.url
                        doOnClick {
                            startFragment<LibraryFragment> { putParam1(library) }
                        }
                    }
                }
            }
            logo()
        }

    }


}