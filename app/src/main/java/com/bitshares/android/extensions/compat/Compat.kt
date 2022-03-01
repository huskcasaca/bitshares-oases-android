package com.bitshares.android.extensions.compat

import bitshareskit.models.Market
import bitshareskit.objects.AccountBalanceObject
import bitshareskit.objects.AccountObject
import bitshareskit.operations.Operation
import com.bitshares.android.chain.IntentParameters
import com.bitshares.android.database.entities.User
import com.bitshares.android.preference.old.Settings
import com.bitshares.android.ui.account.browser.AccountBrowserFragment
import com.bitshares.android.ui.account.importer.ImportFragment
import com.bitshares.android.ui.account.keychain.KeychainFragment
import com.bitshares.android.ui.account.margin.CollateralFragment
import com.bitshares.android.ui.account.margin.MarginPositionFragment
import com.bitshares.android.ui.account.membership.MembershipFragment
import com.bitshares.android.ui.account.permission.PermissionFragment
import com.bitshares.android.ui.account.voting.VotingFragment
import com.bitshares.android.ui.account.whitelist.WhitelistFragment
import com.bitshares.android.ui.asset.browser.AssetBrowserFragment
import com.bitshares.android.ui.base.putJson
import com.bitshares.android.ui.base.startFragment
import com.bitshares.android.ui.block.BlockBrowserFragment
import com.bitshares.android.ui.faucet.FaucetFragment
import com.bitshares.android.ui.settings.SettingsFragment
import com.bitshares.android.ui.trading.TradingFragment
import com.bitshares.android.ui.transaction.operation_browser.OperationBrowserFragment
import com.bitshares.android.ui.transfer.TransferFragment
import modulon.union.Union
import modulon.union.UnionContext
import java.math.BigDecimal


//fun UnionContext.startAccountBrowser(uid: ULong) = startFragment<AccountBrowserFragment>{ putExtraJson(IntentParameters.Account.KEY_UID, uid) }
fun UnionContext.startAccountBrowser(uid: Long) = startFragment<AccountBrowserFragment>{ putJson(IntentParameters.Account.KEY_UID, uid) }

fun UnionContext.startAssetBrowser(uid: Long) = startFragment<AssetBrowserFragment> {
    putJson(IntentParameters.Asset.KEY_UID, uid)
}
fun UnionContext.startBlockBrowser(uid: Long) = startFragment<BlockBrowserFragment> {
    putJson(IntentParameters.Block.KEY_HEIGHT, uid)
}

// FIXME: 2022/2/18 Operation serialization
// FIXME: 2022/2/19
fun UnionContext.startOperationBrowser(operation: Operation) = startFragment<OperationBrowserFragment> {
    putJson(IntentParameters.Operation.KEY_OPERATION, operation)
}

fun UnionContext.startMarketTrade(market: Market) = startFragment<TradingFragment> {
    putJson(IntentParameters.MarketTrade.KEY_MARKET, market)
}

fun UnionContext.startImport() = startFragment<ImportFragment>()
fun UnionContext.startSettings() = startFragment<SettingsFragment>()
fun UnionContext.startRegister() = startFragment<FaucetFragment>()

fun UnionContext.startKeychain(uid: Long) = startFragment<KeychainFragment> {
    putJson(IntentParameters.Account.KEY_UID, uid)
}
fun UnionContext.startKeychain(user: User) = startFragment<KeychainFragment> {
    putJson(IntentParameters.Account.KEY_UID, user.uid)
    putJson(IntentParameters.Chain.KEY_CHAIN_ID, user.chainId)
}
fun UnionContext.startPermission(uid: Long) = startFragment<PermissionFragment> {
    putJson(IntentParameters.Account.KEY_UID, uid)
}
fun UnionContext.startWhitelist(uid: Long) = startFragment<WhitelistFragment>(){
    putJson(IntentParameters.Account.KEY_UID, uid)
}
fun UnionContext.startMarginPosition(uid: Long) = startFragment<MarginPositionFragment> {
    putJson(IntentParameters.Account.KEY_UID, uid)
}
fun UnionContext.startVoting(uid: Long) = startFragment<VotingFragment> {
    putJson(IntentParameters.Account.KEY_UID, uid)
}
fun UnionContext.startMembership(uid: Long) = startFragment<MembershipFragment> {
    putJson(IntentParameters.Account.KEY_UID, uid)
}
fun UnionContext.startCollateral(uid: Long) = startFragment<CollateralFragment> {
    putJson(IntentParameters.Account.KEY_UID, uid)
}
fun UnionContext.startCollateral(uid: Long, assetUid: Long) = startFragment<CollateralFragment> {
    putJson(IntentParameters.Account.KEY_ACCOUNT, uid)
    putJson(IntentParameters.Asset.KEY_ASSET, assetUid)
}
fun UnionContext.startCollateralWithCallOrder(uid: Long) = startFragment<CollateralFragment> {
    putJson(IntentParameters.CallOrder.KEY_CALL_ORDER, uid)
}

fun Union.startTransferFrom(uid: Long) = startTransfer(from = uid)
fun Union.startTransferTo(account: AccountObject) = startTransfer(from = Settings.KEY_CURRENT_ACCOUNT_ID.value, to = account.uid)
fun Union.startTransferBalance(balance: AccountBalanceObject) = startTransfer(from = balance.ownerUid, balance = balance.uid)

//fun Union.startTransfer(block: TransferBuilder.() -> Unit) = startActivityExtras<TransferActivity>(*TransferBuilder().apply(block).build())
//fun Union.startActivityExtras<TransferActivity>(*TransferBuilder(from, to, buyAmount, asset, memo).build())
fun Union.startTransfer(from: Long? = null, to: Long? = null, amount: BigDecimal? = null, asset: Long? = null, balance: Long? = null, memo: String? = null) {
    startFragment<TransferFragment> {
        putJson(IntentParameters.Transfer.KEY_FROM, from)
        putJson(IntentParameters.Transfer.KEY_TO, to)
        putJson(IntentParameters.Transfer.KEY_AMOUNT, amount)
        putJson(IntentParameters.Transfer.KEY_ASSET, asset)
        putJson(IntentParameters.Transfer.KEY_BALANCE, balance)
        putJson(IntentParameters.Transfer.KEY_MEMO, memo)
    }
}




