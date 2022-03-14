package graphene.chain

import graphene.protocol.*

abstract class K000AbstractObject(
) : Cloneable, K000AbstractType {

    companion object {
        const val KEY_ID = "id"
    }

    abstract override val id: K000AbstractId

}