package com.bitshares.oases.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.ViewModelIntent
import com.bitshares.oases.extensions.lifecycle.viewModels
import com.bitshares.oases.ui.account.picker.AccountBalancePickerFragment
import com.bitshares.oases.ui.account.picker.AccountPickerFragment
import com.bitshares.oases.ui.asset.picker.AssetPickerFragment
import com.bitshares.oases.ui.main.MainFragment
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.serializersModuleOf
import modulon.extensions.compat.startActivity
import modulon.extensions.compat.startActivityForResult
import modulon.extensions.content.getExtra
import modulon.extensions.view.*
import modulon.extensions.viewbinder.createFragmentContainer
import modulon.extensions.viewbinder.noClipping
import modulon.union.Union
import modulon.union.UnionContext
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IntentActivity : BaseActivity() {

    private val hasIntentResolved = AtomicBoolean(false)

    // TODO: 2022/2/18 add intent filter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentClazz: Class<out Fragment> = intent.getExtra(IntentParameters.KEY_FRAGMENT, MainFragment::class.java)

        createFragmentContainer {
            layoutWidth = MATCH_PARENT
            layoutHeight = MATCH_PARENT

            noClipping()
            ensureViewId()
            addFragment(fragmentClazz)
            setContentView(this)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!hasIntentResolved.getAndSet(true)) {
            viewModelStore.viewModels.values.forEach {
                if (it is ViewModelIntent) it.onActivityIntent(intent)
            }
        }
    }

}

val argumentParams = listOf(
    IntentParameters.KEY_PARAM_1,
    IntentParameters.KEY_PARAM_2,
    IntentParameters.KEY_PARAM_3,
    IntentParameters.KEY_PARAM_4,
    IntentParameters.KEY_PARAM_5,
)

object BigDecimalStringSerializer : KSerializer<BigDecimal> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("decimal", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toPlainString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        val string = decoder.decodeString()
        return BigDecimal(string)
    }
}

val BundleJsonConverter = Json {
    serializersModule = SerializersModule {
        include(serializersModuleOf(BigDecimalStringSerializer))
        include(serializersModuleOf(AccountObject.serializer()))
        include(serializersModuleOf(AssetObject.serializer()))
    }
    encodeDefaults = true
    ignoreUnknownKeys = true
}
// startFragment
inline fun <reified F: Fragment> UnionContext.startFragment(action: Intent.() -> Unit = {}) {
    startActivity(
        Intent(context, IntentActivity::class.java).apply {
            putExtra(IntentParameters.KEY_FRAGMENT, F::class.java)
            action()
        }
    )
}

// startFragmentForResult / suspendFragmentForResult
inline fun <reified F: Fragment> Union.startFragmentForResult(crossinline action: Intent.() -> Unit = {}, crossinline callback: Intent.() -> Unit = {}) {
    startActivityForResult(
        Intent(context, IntentActivity::class.java).apply {
            putExtra(IntentParameters.KEY_FRAGMENT, F::class.java)
            action()
        }
    ) { callback.invoke(it.data ?: Intent()) }
}
suspend inline fun <reified F: Fragment> Union.suspendFragmentForResult(crossinline action: Intent.() -> Unit = {}): Intent = suspendCoroutine { cont ->
    startActivityForResult(
        Intent(context, IntentActivity::class.java).apply {
            putExtra(IntentParameters.KEY_FRAGMENT, F::class.java)
            action()
        }
    ) { cont.resume(it.data ?: Intent()) }
}



// startFragmentForResultContract / suspendFragmentForResultContract
inline fun <I, O> Union.startFragmentForResultContract(contract: ActivityResultContract<I, O>, input: I, crossinline result: (O?) -> Unit) {
    activityResultCaller.registerForActivityResult(contract) { result.invoke(it) }.launch(input)
}
inline fun <O> Union.startFragmentForResultContract(contract: ActivityResultContract<Unit, O>, crossinline result: (O?) -> Unit) {
    activityResultCaller.registerForActivityResult(contract) { result.invoke(it) }.launch(Unit)
}
suspend inline fun <I, O> Union.suspendFragmentForResultContract(contract: ActivityResultContract<I, O>, input: I): O? = suspendCoroutine { cont ->
    activityResultCaller.registerForActivityResult(contract) { cont.resume(it) }.launch(input)
}
suspend inline fun <O> Union.suspendFragmentForResultContract(contract: ActivityResultContract<Unit, O>): O? = suspendCoroutine { cont ->
    activityResultCaller.registerForActivityResult(contract) { cont.resume(it) }.launch(Unit)
}


fun Union.startAccountBalanceIdPicker(uid: Long, result: (Long?) -> Unit) = startFragmentForResultContract(accountBalanceIdContract, uid, result)
fun Union.startAccountIdPicker(result: (Long?) -> Unit) = startFragmentForResultContract(accountIdContract, result)
fun Union.startAssetIdPicker(result: (Long?) -> Unit) = startFragmentForResultContract(assetIdContract, result)

fun Union.startAccountPicker(result: (AccountObject?) -> Unit) = startFragmentForResultContract(accountContract, result)
fun Union.startAssetPicker(result: (AssetObject?) -> Unit) = startFragmentForResultContract(assetContract, result)


suspend fun Union.suspendAccountBalanceIdPicker(uid: Long) = suspendFragmentForResultContract(accountBalanceIdContract, uid)
suspend fun Union.suspendAccountIdPicker(uid: Long) = suspendFragmentForResultContract(accountIdContract)
suspend fun Union.suspendAssetIdPicker(uid: Long) = suspendFragmentForResultContract(assetIdContract)


// TODO: 2022/2/18 removed
// contracts
val accountBalanceIdContract = fragmentContract<AccountBalancePickerFragment, Long, Long>(
    { putJson(IntentParameters.Account.KEY_UID, it) }, { getJsonOrNull(IntentParameters.AccountBalance.KEY_UID) }
)
val accountIdContract = fragmentContract<AccountPickerFragment, Unit, Long>(
    { }, { getJsonOrNull(IntentParameters.Account.KEY_UID) }
)
val assetIdContract = fragmentContract<AssetPickerFragment, Unit, Long>(
    { }, { getJsonOrNull(IntentParameters.Asset.KEY_UID) }
)
val accountContract = fragmentContract<AccountPickerFragment, Unit, AccountObject>(
    { }, { getJsonOrNull(IntentParameters.Account.KEY_ACCOUNT) }
)
val assetContract = fragmentContract<AssetPickerFragment, Unit, AssetObject>(
    { }, { getJsonOrNull(IntentParameters.Asset.KEY_ASSET) }
)

private inline fun <reified F, reified I, reified O> fragmentContract(
    crossinline action: Intent.(I) -> Unit,
    crossinline result: Intent.() -> O?
) = object : ActivityResultContract<I, O?>() {
    override fun createIntent(context: Context, input: I): Intent {
        return Intent(context, IntentActivity::class.java).apply {
            putExtra(IntentParameters.KEY_FRAGMENT, F::class.java)
            action(this, input)
        }
    }
    override fun parseResult(resultCode: Int, intent: Intent?): O? {
        return if (intent != null) result(intent) else null
    }
}



inline fun <reified T> Intent.putJson(key: String, value: T) {
    putExtra(key, BundleJsonConverter.encodeToString(value))
}
inline fun <reified T> Intent.getJson(key: String): T {
    return BundleJsonConverter.decodeFromString(getExtra(key))
}
inline fun <reified T> Intent.getJsonOrNull(key: String): T? {
    return runCatching { BundleJsonConverter.decodeFromString<T>(getExtra(key)) }.getOrNull()
}
inline fun <reified T> Intent.getJson(key: String, defaultValue: T): T {
    return runCatching {  BundleJsonConverter.decodeFromString<T>(getExtra(key)) }.getOrDefault(defaultValue)
}
inline fun <reified T> Fragment.activityJsonExtras(key: String): Lazy<T> = lazy {
    requireActivity().intent.getJson(key)
}


inline fun <reified T> Fragment.activityParam1(): Lazy<T> = activityJsonExtras(IntentParameters.KEY_PARAM_1)
inline fun <reified T> Intent.putParam1(value: T) = putJson(IntentParameters.KEY_PARAM_1, value)




@Serializable
data class TransferParams(
    val from: Long? = null,
    val to: Long? = null,
    @Serializable(with = BigDecimalStringSerializer::class)
    val amount: BigDecimal? = null,
    val asset: Long? = null,
    val balance: Long? = null,
    val memo: String? = null
)

