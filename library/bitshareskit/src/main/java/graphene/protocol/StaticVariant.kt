package graphene.protocol

import graphene.serializers.StaticVariantSerializer
import kotlinx.serialization.Serializable

@Serializable(with = StaticVariantSerializer::class)
data class StaticVariant<out T>(
    val tagType: Int64,
    val storage: T
) : Comparable<StaticVariant<@UnsafeVariance T>>, GrapheneComponent {

    // TODO: 2022/4/1 move to types
    override fun compareTo(other: StaticVariant<@UnsafeVariance T>): Int {
        return tagType.compareTo(other.tagType)
    }
}
