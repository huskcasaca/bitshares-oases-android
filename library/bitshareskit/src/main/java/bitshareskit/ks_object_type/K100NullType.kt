package bitshareskit.ks_object_type

import bitshareskit.ks_object_base.K100NullId

interface K100NullType: K000AbstractType {
    override val id: K100NullId get() = this as K100NullId
}