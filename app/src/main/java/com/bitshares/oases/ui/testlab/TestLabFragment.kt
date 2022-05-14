package com.bitshares.oases.ui.testlab

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import graphene.chain.K102_AccountObject
import graphene.chain.K103_AssetObject
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.oases.R
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.provider.chain_repo.GrapheneRepository
import com.bitshares.oases.ui.asset.browser.actionBarLayout
import com.bitshares.oases.ui.asset.browser.bodyCoordinatorParams
import com.bitshares.oases.ui.asset.browser.actionCoordinatorParams
import com.bitshares.oases.ui.base.ContainerFragment
import graphene.protocol.*
import graphene.serializers.GRAPHENE_JSON_PLATFORM_SERIALIZER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import modulon.component.cell.ComponentCell
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.component.appbar.subtitle
import modulon.component.appbar.title
import modulon.component.cell.toggleEnd
import modulon.layout.lazy.*
import modulon.component.tab.tab
import modulon.extensions.compat.recreateActivity
import modulon.widget.PlainTextView

class TestLabFragment : ContainerFragment() {

    private val viewModel: TestLabViewModel by activityViewModels()

    fun Any.console() = viewModel.console(System.currentTimeMillis(), this)

    override fun ViewGroup.onCreateView() {
        fitsSystemWindows = true
        actionBarLayout {
            layoutParams = actionCoordinatorParams()
            title(context.getString(R.string.about_title))
            subtitle(AppConfig.APP_NAME)
        }
        verticalLayout {
            layoutParams = bodyCoordinatorParams()
            tabLayout {
                tab { text = "General" }
                tab { text = "Serialization" }
                tab { text = "Ktor Test" }
                tab { text = "Console" }
                post { attachViewPager2(nextView()) }
            }
            pagerLayout {
                post { setCurrentItem(1, false) }
                attachFragmentListAdapter((0..3).toList()) {
                    when (it) {
                        0 -> TestCaseAFragment()
                        1 -> TestCaseBFragment()
                        2 -> TestCaseCFragment()
                        3 -> TestCaseDFragment()
                        else -> TestCaseDFragment()
                    }
                }
            }
        }

    }

}

fun LazyListView.testSettings() {
    section {
        header = "Test Settings"
        cell {
            text = ""
        }
    }
}