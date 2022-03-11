package com.bitshares.oases.ui.main.explore

import bitshareskit.entities.Block
import bitshareskit.objects.WitnessObject
import bitshareskit.objects.WorkerObject
import com.bitshares.oases.chain.CommitteeMember

// FIXME: 27/1/2022 fix filter

fun Block.containsInternal(other: CharSequence) = blockNum.toString().contains(other) || rootHash.contains(other) || witness.witnessAccount.name.contains(other, true)

fun CommitteeMember.containsInternal(other: CharSequence) = account.name.contains(other, true) || committee.id.contains(other, true)

fun WitnessObject.containsInternal(other: CharSequence) = witnessAccount.name.contains(other, true) || witnessAccount.id.contains(other, true) || id.contains(other, true)

fun WorkerObject.containsInternal(other: CharSequence) = workerAccount.name.contains(other, true) || id.contains(other, true)