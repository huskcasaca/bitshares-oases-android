package com.bitshares.oases

import android.content.Context
import android.net.ConnectivityManager
import android.view.Display
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import com.bitshares.oases.database.BlockchainDatabase
import com.bitshares.oases.database.LocalDatabase
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.netowrk.java_websocket.SocketConnectionManager
import com.bitshares.oases.preference.ChainPreferenceManager
import com.bitshares.oases.preference.PreferenceManager
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.security.WalletSecurityManager
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


        lateinit var WALLET: WalletSecurityManager
    }

    val settingsManager by lazy { PreferenceManager(this) }
    val chainSettingsManager by lazy { ChainPreferenceManager(this) }
    val walletSecurityManager: WalletSecurityManager by lazy { WalletSecurityManager(this) }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        WALLET = walletSecurityManager
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

    val connectivityManager by lazy { getSystemService<ConnectivityManager>() }
    val socketConnectionManager by lazy { getSystemService<SocketConnectionManager>() }

}

val Union.applicationSettingsManager get() = (activity.application as MainApplication).settingsManager
val Union.applicationConnectivityManager get() = (activity.application as MainApplication).connectivityManager
val Union.applicationWalletSecurityManager get() = (activity.application as MainApplication).walletSecurityManager

val AndroidViewModel.applicationSettingsManager get() = getApplication<MainApplication>().settingsManager
val AndroidViewModel.applicationConnectivityManager get() = getApplication<MainApplication>().connectivityManager
val AndroidViewModel.applicationWalletSecurityManager get() = getApplication<MainApplication>().walletSecurityManager