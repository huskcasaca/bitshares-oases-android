package com.bitshares.android.ui.settings

import android.app.Application
import androidx.lifecycle.viewModelScope
import bitshareskit.extensions.formatAssetInteger
import bitshareskit.extensions.ifNull
import bitshareskit.models.AssetAmount
import com.bitshares.android.MainApplication
import com.bitshares.android.chain.blockchainDatabaseScope
import com.bitshares.android.database.BlockchainDatabase
import com.bitshares.android.preference.AppConfig
import com.bitshares.android.preference.old.Graphene
import com.bitshares.android.preference.old.Settings
import com.bitshares.android.provider.chain_repo.AssetRepository
import com.bitshares.android.provider.local_repo.LocalUserRepository
import com.bitshares.android.provider.local_repo.SystemBuildRepository.getSystemABIs
import com.bitshares.android.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.livedata.*

class SettingsViewModel(application: Application) : BaseViewModel(application) {

    val account = LocalUserRepository.currentUserAccount
    val language = Settings.KEY_LANGUAGE

    // fee reserve dialog
    val feeReserved = combineNonNull(Settings.KEY_RESERVED_FEE, Graphene.KEY_CORE_ASSET) { amount, asset -> AssetAmount(amount, asset) }
    val feeReservedExtended = combineNonNull(Settings.KEY_AUTO_RESERVE_FEE, Settings.KEY_RESERVED_FEE, Graphene.KEY_CORE_ASSET) { auto, amount, asset -> if (auto) AssetAmount.EMPTY else AssetAmount(amount, asset) }

    val isAutoReserveChecked = NonNullMediatorLiveData(Settings.KEY_AUTO_RESERVE_FEE.value)
    val feeReservedField = NonNullMediatorLiveData(EMPTY_SPACE)


    //    val feeReservedField = NonNullMediatorLiveData(EMPTY_SPACE)
    val isFeeReservedFieldError = NonNullMediatorLiveData(false)

    //    var feeReservedFieldText
//        get() = feeReservedField.value
//        set(value) {
//            feeReservedField.value = value
//            isFeeReservedFieldError.value = false
//        }
    var feeReservedFieldText = EMPTY_SPACE

    fun checkDismiss(): Boolean {
        val fee = feeReservedFieldText.toBigDecimalOrNull()
        if (!isAutoReserveChecked.value && fee != null || isAutoReserveChecked.value) {
            Settings.KEY_AUTO_RESERVE_FEE.value = !Settings.KEY_AUTO_RESERVE_FEE.value
            Settings.KEY_RESERVED_FEE.value = if (fee == null) Settings.KEY_RESERVED_FEE.defaultValue else formatAssetInteger(fee, Graphene.KEY_CORE_ASSET.value)
        } else {
            isFeeReservedFieldError.value = true
        }
        return !isAutoReserveChecked.value && fee != null || isAutoReserveChecked.value
    }

    val priceUnits = Settings.KEY_MARKET_GROUPS.default.mapChildParallel(viewModelScope) { AssetRepository.getAssetWithBitassetDataOrNull(it) }.filterChildNotNull()
    val priceUnit = Settings.KEY_BALANCE_UNIT.map(viewModelScope) { AssetRepository.getAssetWithBitassetDataOrNull(it) }.filterNotNull()

    private val blockchainDatabaseSize
        get() = getApplication<MainApplication>().getDatabasePath(BlockchainDatabase.DB_NAME)?.length() ?: 0L

    val blockchainDBSize = NonNullMutableLiveData(blockchainDatabaseSize)

    fun clearBlockchainDatabase() {
        blockchainDatabaseScope.launch {
            BlockchainDatabase.INSTANCE.apply {
                clearAllTables()
                viewModelScope.launch(Dispatchers.Main) { blockchainDBSize.postValue(blockchainDatabaseSize) }
            }
        }
    }

    val appDescription = NonNullMutableLiveData(
        "${AppConfig.APP_PACKAGE_NAME} v${AppConfig.APP_VERSION} ${getSystemABIs().firstOrNull().ifNull { "unknown_abi" }}"
    )

}

