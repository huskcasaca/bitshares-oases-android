package bitshareskit.ks_object_base

import bitshareskit.ks_objects.K000AbstractType
import bitshareskit.ks_objects.ObjectInstance

abstract class K000AbstractId : Cloneable, K000AbstractType {

    abstract val space: ObjectSpace
    abstract val type: ObjectType
    abstract val instance: ObjectInstance
}


