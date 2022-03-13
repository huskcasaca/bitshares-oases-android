package bitshareskit.ks_objects

import bitshareskit.ks_object_base.*
import bitshareskit.ks_object_type.K000AbstractType

abstract class K000AbstractObject(
) : Cloneable, K000AbstractType {

    companion object {
        const val KEY_ID = "id"
    }

    abstract override val id: K000AbstractId

}