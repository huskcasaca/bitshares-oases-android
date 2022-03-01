package bitshareskit.ks_objects

import bitshareskit.ks_object_base.K000AbstractId
import bitshareskit.ks_object_base.UInt64
import bitshareskit.ks_object_base.UInt8

interface K000AbstractType {
    val id: K000AbstractId
    val space: UInt8
    val type: UInt8
    val instance: UInt64
}
