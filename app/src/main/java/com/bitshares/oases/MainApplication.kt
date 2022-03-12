package com.bitshares.oases

import android.content.Context
import android.net.ConnectivityManager
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
import com.bitshares.oases.security.WalletManager
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import modulon.extensions.compat.application
import modulon.union.ModulonApplication
import modulon.union.Union
import modulon.union.UnionContext

class MainApplication : ModulonApplication() {

    companion object {

        // TODO: 2022/3/11 remove
        lateinit var context: Context
        // TODO: 2022/3/11 remove
        fun requireContext() = context

        val applicationJob: CompletableJob = Job()

//        val applicationScope: CoroutineScope = CoroutineScope(Dispatchers.IO + applicationJob)

    }

    val preferenceManager by lazy { PreferenceManager(this) }
    val chainPreferenceManager by lazy { ChainPreferenceManager(this) }
    val walletManager: WalletManager by lazy { WalletManager(this) }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        LocalDatabase.initialize(applicationContext)
        BlockchainDatabase.initialize(applicationContext)

        AppCompatDelegate.setDefaultNightMode(preferenceManager.DARK_MODE.value.mode)
        preferenceManager.DARK_MODE.observeForever {
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

    val connectivityManager by lazy { getSystemService<ConnectivityManager>() }
    val socketConnectionManager by lazy { getSystemService<SocketConnectionManager>() }

}

val Union.globalPreferenceManager get() = (activity.application as MainApplication).preferenceManager
val Union.globalWalletManager get() = (activity.application as MainApplication).walletManager

val UnionContext.globalPreferenceManager get() = (context.application as MainApplication).preferenceManager
val UnionContext.globalWalletManager get() = (context.application as MainApplication).walletManager

val AndroidViewModel.globalPreferenceManager get() = getApplication<MainApplication>().preferenceManager
val AndroidViewModel.globalConnectivityManager get() = getApplication<MainApplication>().connectivityManager
val AndroidViewModel.globalWalletManager get() = getApplication<MainApplication>().walletManager

val UnionContext.localPreferenceManager get() = PreferenceManager(context)





