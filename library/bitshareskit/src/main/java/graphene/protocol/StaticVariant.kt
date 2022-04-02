package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable(with = StaticVariantSerializer::class)
open class StaticVariant<T>(
    open val tagType: Int64,
    open val storage: T
) : Comparable<StaticVariant<T>>, GrapheneComponent {

    // TODO: 2022/4/1 move to types
    override fun compareTo(other: StaticVariant<T>): Int {
        return tagType.compareTo(other.tagType)
    }
}