package com.bitshares.android

import android.content.Context
import android.net.ConnectivityManager
import android.view.Display
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import com.bitshares.android.database.BlockchainDatabase
import com.bitshares.android.database.LocalDatabase
import com.bitshares.android.netowrk.java_websocket.NetworkService
import com.bitshares.android.netowrk.java_websocket.SocketConnectionManager
import com.bitshares.android.preference.ChainPreferenceManager
import com.bitshares.android.preference.PreferenceManager
import com.bitshares.android.provider.chain_repo.ChainPropertyRepository
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import modulon.union.ModulonApplication
import modulon.union.Union

class MainApplication : ModulonApplication() {

    companion object {
        lateinit var context: Context

        fun requireContext() = context

        val applicationJob: CompletableJob = Job()
//        val applicationScope: CoroutineScope = CoroutineScope(Dispatchers.IO + applicationJob)
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        LocalDatabase.initialize(applicationContext)
        BlockchainDatabase.initialize(applicationContext)


        AppCompatDelegate.setDefaultNightMode(settingsManager.KEY_DARK_MODE.value.mode)
        settingsManager.KEY_DARK_MODE.observeForever {
            AppCompatDelegate.setDefaultNightMode(it.mode)
        }

        NetworkService.start()

        getSystemService<ConnectivityManager>()?.registerDefaultNetworkCallback(NetworkService)
        registerActivityLifecycleCallbacks(NetworkService)
        ChainPropertyRepository.start()
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationJob.cancel()
    }

    override fun createDisplayContext(display: Display): Context {
        return super.createDisplayContext(display)
    }

    val settingsManager by lazy { PreferenceManager(this) }
    val chainSettingsManager by lazy { ChainPreferenceManager(this) }

    val connectivityManager by lazy { getSystemService<ConnectivityManager>() }
    val socketConnectionManager by lazy { getSystemService<SocketConnectionManager>() }

}

val Union.applicationSettingsManager get() = (activity.application as MainApplication).settingsManager
val Union.applicationConnectivityManager get() = (activity.application as MainApplication).connectivityManager