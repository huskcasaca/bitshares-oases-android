package graphene.protocol

import kotlinx.serialization.Serializable

@Serializable(with = StaticVariantSerializer::class)
abstract class StaticVariant<T> {
    abstract val tagType: Int64
    abstract val storage: T
}