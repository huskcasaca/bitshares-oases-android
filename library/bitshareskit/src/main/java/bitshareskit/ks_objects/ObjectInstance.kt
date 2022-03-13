package bitshareskit.ks_objects

import bitshareskit.ks_object_base.UInt64

@JvmInline
value class ObjectInstance(
    val id: UInt64
) {
    companion object {
        val INVALID_ID = ObjectInstance(UInt64.MAX_VALUE)
    }
}
