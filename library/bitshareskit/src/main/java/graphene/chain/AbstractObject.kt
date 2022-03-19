package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.Serializable

@Serializable(with = AbstractObjectSerializer::class)
sealed class AbstractObject(
) : Cloneable, AbstractType {

    companion object {
        const val KEY_ID = "id"
    }

    abstract override val id: AbstractIdType

}


