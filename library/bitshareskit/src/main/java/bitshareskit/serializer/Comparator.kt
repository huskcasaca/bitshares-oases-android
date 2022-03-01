package bitshareskit.serializer

import bitshareskit.extensions.toHexString
import bitshareskit.models.PublicKey
import bitshareskit.models.Vote
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.GrapheneSerializable

val grapheneInstanceDistinctor = { o1: GrapheneObject?, o2: GrapheneObject? ->
    o1?.uid == o2?.uid && o1?.objectType == o2?.objectType
}

val publicKeyComparator = Comparator<PublicKey> { o1, o2 -> o1.addressBytes.toHexString().compareTo(o2.addressBytes.toHexString()) }

val grapheneInstanceComparator = Comparator<GrapheneObject> { o1, o2 -> o1.uid.compareTo(o2.uid) }
val voteComparator = Comparator<Vote> { o1, o2 -> o1.instance.compareTo(o2.instance) }
val binaryComparator = Comparator<GrapheneSerializable> { o1, o2 -> o1.toByteArray().toHexString().compareTo(o2.toByteArray().toHexString()) }

val grapheneSerializableComparator = Comparator<GrapheneSerializable> { o1, o2  ->
    when {
        o1 is PublicKey && o2 is PublicKey -> publicKeyComparator.compare(o1, o2)
        o1 is GrapheneObject && o2 is GrapheneObject -> grapheneInstanceComparator.compare(o1, o2)
        o1 is Vote && o2 is Vote -> voteComparator.compare(o1, o2)
        else -> binaryComparator.compare(o1, o2)
    }
}

val grapheneGlobalComparator = Comparator<Any> { o1, o2  ->
    when {
        o1 is PublicKey && o2 is PublicKey -> publicKeyComparator.compare(o1, o2)
        o1 is GrapheneObject && o2 is GrapheneObject -> grapheneInstanceComparator.compare(o1, o2)
        o1 is Vote && o2 is Vote -> voteComparator.compare(o1, o2)
        o1 is GrapheneSerializable && o2 is GrapheneSerializable -> binaryComparator.compare(o1, o2)
        o1 is Comparable<*> && o2 is Comparable<*> -> compareValues(o1, o2)
        else -> TODO()
    }
}