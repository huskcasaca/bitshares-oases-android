package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.Serializable

@Serializable(with = AbstractObjectSerializer::class)
sealed class AbstractObject(
) : Cloneable, AbstractType {

    abstract override val id: AbstractIdType

}


