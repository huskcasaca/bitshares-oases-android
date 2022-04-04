package graphene.chain

import graphene.protocol.*
import graphene.serializers.ObjectSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ObjectSerializer::class)
sealed class AbstractObject(
) : ObjectIdType {

    abstract override val id: ObjectId
    override val space: ObjectSpace get() = id.space
    override val type: ObjectType get() = id.type
    override val instance: ObjectInstance get() = id.instance
    override val number: ObjectInstance get() = id.number

}


