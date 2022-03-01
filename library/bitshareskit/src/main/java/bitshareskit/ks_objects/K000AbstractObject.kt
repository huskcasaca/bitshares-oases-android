package bitshareskit.ks_objects

import bitshareskit.ks_object_base.K000AbstractId
import bitshareskit.ks_object_base.UInt64
import bitshareskit.ks_object_base.UInt8

abstract class K000AbstractObject : Cloneable, K000AbstractType {

    companion object {
        const val TABLE_NAME = "graphene_object"

        const val COLUMN_OWNER_UID = "owner_uid"
        const val COLUMN_LAST_UPDATE = "last_update"
        const val COLUMN_UID = "uid"

        const val KEY_ID = "id"
    }

    abstract override val id: K000AbstractId

    final override val space: UInt8 get() = id.space
    final override val type: UInt8 get() = id.type
    final override val instance: UInt64 get() = id.instance

}


