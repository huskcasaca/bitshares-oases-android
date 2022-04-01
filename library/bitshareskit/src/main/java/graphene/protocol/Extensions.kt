package graphene.protocol

import graphene.chain.AbstractObject
import graphene.chain.K102_AccountObject
import kotlinx.serialization.SerialName


private fun String.toVote(): VoteIdType {
    return VoteIdType.fromStringId(this)
}

fun ULong.toAccount() = K102_AccountIdType(this)

val AbstractType.standardId: String
    get() = "${id.space.id}$GRAPHENE_ID_SEPARATOR${id.type.id}$GRAPHENE_ID_SEPARATOR${id.instance}"

val AbstractType.isObject
    get() = this is AbstractObject

val AbstractType.isObjectId
    get() = this !is AbstractObject

val AbstractType.isValid
    get() = id.type != ProtocolType.NULL && id.instance < 0xFFFFFFFFFFFFFFUL


inline fun <reified T: AbstractObject> T?.orEmpty(): T = this ?: when (T::class) {
    K102_AccountObject::class -> INVALID_ACCOUNT_OBJECT
    else -> TODO()
} as T

inline fun <reified T: AbstractIdType> T?.orEmpty(): T = this ?: emptyIdType()

