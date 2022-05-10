package com.bitshares.oases.ui.about

import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.preference.Library
import com.bitshares.oases.ui.account.permission.PermissionViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.activityParam1
import modulon.extensions.compat.startUriBrowser
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.view.doOnClick
import modulon.extensions.view.view
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.horizontalScrollLayout
import modulon.component.appbar.subtitle
import modulon.component.appbar.title
import modulon.layout.lazy.section
import modulon.widget.PlainTextView

class LibraryFragment : ContainerFragment() {

    private val viewModel: PermissionViewModel by activityViewModels()
    private val library: Library by activityParam1()

    override fun onCreateView() {
        setupAction {
            title("Library")
            subtitle(library.name)
        }
        setupRecycler {
            section {
                cell {
                    title = "Library Name"
                    subtitle = library.name
                }
                cell {
                    title = "Version"
                    subtitle = ""
                }
                cell {
                    title = "Author"
                    subtitle = ""
                }
                cell {
                    title = "License"
                    subtitle = library.license
                }
                cell {
                    title = "Website"
                    subtitle = "Goto"
                    doOnClick {
                        startUriBrowser(library.url.toUri())
                    }
                }
            }
            section {
                header = "License"
                cell {
                    customHorizontal {
                        horizontalScrollLayout {
                            view<PlainTextView> {
                                text = library.licenseFile
                                typeface = typefaceMonoRegular
                                textSize = 12f
                            }
                        }

                    }
                }
            }
            logo()
        }

    }


}