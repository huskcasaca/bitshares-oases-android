package bitshareskit.ks_object_base

import bitshareskit.extensions.EMPTY_SPACE
import bitshareskit.extensions.logloglog
import bitshareskit.ks_models.*
import bitshareskit.ks_object_type.K000AbstractType
import bitshareskit.ks_object_type.K102AccountType
import bitshareskit.ks_objects.K103AssetObject
import bitshareskit.ks_objects.ObjectInstance
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


private const val GRAPHENE_ID_SEPARATOR = "."

val String.isValidGrapheneId: Boolean
    get() = matches(Regex("[0-9]+\\${GRAPHENE_ID_SEPARATOR}[0-9]+\\${GRAPHENE_ID_SEPARATOR}[0-9]+")) &&
            split(GRAPHENE_ID_SEPARATOR).let {
                GRAPHENE_ID_TO_SPACE[it[0].toUInt8OrNull() ?: return@let false] ?: return@let false
                GRAPHENE_ID_TO_PROTOCOL_TYPE[it[1].toUInt8OrNull() ?: return@let false] ?: return@let false
                it[0].toUInt64OrNull() ?: return@let false
                true
            }

fun String.toGrapheneSpace(): ObjectSpace {
    if (!isValidGrapheneId) throw IllegalArgumentException("Invalid graphene id!")
    val uid = split(GRAPHENE_ID_SEPARATOR)[0].toUInt8()
    return GRAPHENE_ID_TO_SPACE[uid] ?: throw IllegalArgumentException("Invalid graphene id!")
}

fun String.toGrapheneType(): ObjectType {
    if (!isValidGrapheneId) throw IllegalArgumentException("Invalid graphene id!")
    val uid = split(GRAPHENE_ID_SEPARATOR)[1].toUInt8()
    return GRAPHENE_ID_TO_PROTOCOL_TYPE[uid] ?: throw IllegalArgumentException("Invalid graphene id!")
}

fun String.toGrapheneInstance(): ObjectInstance {
    if (!isValidGrapheneId) throw IllegalArgumentException("Invalid graphene id!")
    val uid = split(GRAPHENE_ID_SEPARATOR)[2].toUInt64()
    return ObjectInstance(uid)
}

fun <T: K000AbstractId> String.toGrapheneObjectId(): T {
    logloglog()
    return GRAPHENE_TYPE_TO_ID_CONSTRUCTOR[toGrapheneType()]!!.call(toGrapheneSpace(),toGrapheneType(), toGrapheneInstance()) as T
}

val K000AbstractType.standardId: String
    get() = "${id.space}$GRAPHENE_ID_SEPARATOR${id.type}$GRAPHENE_ID_SEPARATOR${id.instance}"


