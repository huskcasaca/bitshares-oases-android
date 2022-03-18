package graphene.chain

import graphene.protocol.*

abstract class K000_AbstractObject(
) : Cloneable, AbstractType {

    companion object {
        const val KEY_ID = "id"
    }

    abstract override val id: AbstractIdType

}