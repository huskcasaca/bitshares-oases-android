package bitshareskit.ks_object_base

import bitshareskit.extensions.EMPTY_SPACE
import bitshareskit.ks_models.*
import bitshareskit.ks_objects.K000AbstractType
import bitshareskit.ks_objects.K102AccountType
import bitshareskit.ks_objects.K103AssetObject
import kotlin.reflect.KClass

val idTypes: Map<KClass<out K000AbstractType>, K000AbstractId> =
    mapOf(
        K102AccountType::class to K102AccountId(),
    ) + mapOf(
        K102AccountId::class to K102AccountId(),
    )

inline fun <reified K: K000AbstractType> emptyIdType(): K = idTypes[K::class] as K

val components: Map<KClass<out GrapheneComponent>, GrapheneComponent> =
    mapOf(
        KAuthority::class to KAuthority(),
        KOptions::class to KOptions(),
        KPublicKey::class to KPublicKey(),
        KPrivateKey::class to KPrivateKey(),
        K103AssetObject.Options::class to K103AssetObject.Options(),
    )


inline fun <reified K: GrapheneComponent> emptyComponent(): K = components[K::class] as K

fun emptyString(): String = EMPTY_SPACE

//val DEFAULT_ACCOUNT_EMPTY_SET = emptySet<AccountType>()

//inline fun <reified K> emptyComponent(): K = when (K::class) {
//    KAmount::class -> KAmount.EMPTY
//    else -> throw IllegalArgumentException("No empty component for class [${K::class.simpleName}].")
//} as K


