package bitshareskit.ks_object_type

import bitshareskit.ks_object_base.*

interface K101BaseType: K000AbstractType {
    override val id: K101BaseId get() = this as K101BaseId
}