package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable(with = StaticVariantSerializer::class)
open class StaticVariant<T>(
    open val tagType: Int64,
    open val storage: T
) : Comparable<StaticVariant<T>> {

    // TODO: 2022/4/1 move to types
    override fun compareTo(other: StaticVariant<T>): Int {
        return tagType.compareTo(other.tagType)
    }
}

@Serializable
sealed class FeeParameter

@Serializable
object EmptyFeeParameter : FeeParameter()


object TypedFeeParameterSerializer : StaticVariantSerializer<FeeParameter>(FeeParameter.serializer()) {

    override fun selectSerializer(tag: Int64): KSerializer<out FeeParameter> {
        return when (tag) {
            0L -> EmptyFeeParameter.serializer()
            else -> EmptyFeeParameter.serializer()
        }
    }
}