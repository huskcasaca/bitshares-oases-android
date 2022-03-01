package bitshareskit.ks_object_base

import bitshareskit.extensions.ifNull
import bitshareskit.ks_objects.K000AbstractType
import bitshareskit.ks_objects.ObjectInstance

abstract class K000AbstractId : Cloneable, K000AbstractType {

    override val space: UInt8 = KObjectSpace.RELATED_SPACE.space
    override val type: UInt8 = KObjectSpaceType.NULL_OBJECT.type
    override val instance: UInt64 = ObjectInstance.INVALID_ID

    companion object {

        private const val GRAPHENE_ID_SEPARATOR = "."

        fun String.toGrapheneSpace(): UInt8 = split(GRAPHENE_ID_SEPARATOR).getOrNull(0)?.toUInt8OrNull().ifNull { KObjectSpace.RELATED_SPACE.space }
        fun String.toGrapheneType(): UInt8 = split(GRAPHENE_ID_SEPARATOR).getOrNull(1)?.toUInt8OrNull().ifNull { KObjectSpaceType.NULL_OBJECT.type }
        fun String.toGrapheneInstance(): UInt64 = split(GRAPHENE_ID_SEPARATOR).getOrNull(2)?.toUInt64OrNull().ifNull { ObjectInstance.INVALID_ID }

//        fun String.toObjectSpace(): KObjectSpace = when (toObjectSpaceId()) {
//            KObjectSpace.PROTOCOL_SPACE.id -> KObjectSpace.PROTOCOL_SPACE
//            KObjectSpace.IMPLEMENTATION_SPACE.id -> KObjectSpace.IMPLEMENTATION_SPACE
//            KObjectSpace.ACCOUNT_HISTORY_SPACE.id -> KObjectSpace.ACCOUNT_HISTORY_SPACE
//            else -> KObjectSpace.RELATED_SPACE
//        }

//        fun String.toObjectType(): KObjectSpaceId = when (toObjectSpaceId()) {
//            KObjectSpace.PROTOCOL_SPACE.id -> KObjectSpaceId.PROTOCOL_TYPES
//            KObjectSpace.IMPLEMENTATION_SPACE.id -> KObjectSpaceId.IMPLEMENTATION_TYPES
//            KObjectSpace.ACCOUNT_HISTORY_SPACE.id -> KObjectSpaceId.ACCOUNT_HISTORY_TYPES
//            else -> emptyMap()
//        }.getOrDefault(toObjectTypeId(), KObjectSpaceId.NULL_OBJECT)

        fun String.toGrapheneSpaceType(): KObjectSpaceType = KObjectSpaceType.OBJECT_TYPES.getOrDefault(toGrapheneSpace() to toGrapheneType(), KObjectSpaceType.NULL_OBJECT)


        val K000AbstractId.standardId: String
            get() = "$space$GRAPHENE_ID_SEPARATOR$type$GRAPHENE_ID_SEPARATOR$instance"

    }

}


