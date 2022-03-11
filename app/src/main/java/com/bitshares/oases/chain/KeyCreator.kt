package com.bitshares.oases.chain

import android.util.Log
import bitshareskit.models.BrainKey
import bitshareskit.models.PrivateKey
import com.bitshares.oases.preference.old.Graphene
import modulon.extensions.charset.EMPTY_SPACE
import java.util.*

object KeyCreator {

    private val BRAIN_KEY_SPILT_PATTERN = Regex("[^a-zA-Z\\u4e00-\\u9fa5]+|\\r?\\n|\\r")
    private val CHINESE_WORDS_PATTERN = Regex("[\\u4e00-\\u9fa5]+")

    fun createOwnerFromSeed(username: String, seed: String, prefix: String = Graphene.KEY_SYMBOL.value): PrivateKey {
        return PrivateKey.fromSeed("${username}owner${seed}", prefix)
    }

    fun createActiveFromSeed(username: String, seed: String, prefix: String = Graphene.KEY_SYMBOL.value): PrivateKey {
        return PrivateKey.fromSeed("${username}active${seed}", prefix)
    }

    fun createMemoFromSeed(username: String, seed: String, prefix: String = Graphene.KEY_SYMBOL.value): PrivateKey {
        return PrivateKey.fromSeed("${username}memo${seed}", prefix)
    }

    fun createFromSeed(username: String, seed: String, prefix: String = Graphene.KEY_SYMBOL.value): Set<PrivateKey> {
        val ownerKey = PrivateKey.fromSeed("${username}owner${seed}", prefix)
        val activeKey = PrivateKey.fromSeed("${username}active${seed}", prefix)
        val memoKey = PrivateKey.fromSeed("${username}memo${seed}", prefix)
        return setOf(ownerKey, activeKey, memoKey)
    }

    fun createFromMnemonic(seed: String, prefix: String = Graphene.KEY_SYMBOL.value): Set<BrainKey> {
        var formatted = seed.split(BRAIN_KEY_SPILT_PATTERN).filter { it.isNotBlank() }
        if (formatted.all { it.matches(CHINESE_WORDS_PATTERN) }) formatted = formatted.joinToString(EMPTY_SPACE).map { it.toString() }
        Log.d("KeyCreator", "createFromMnemonic: formatted $formatted")
        val wordList = setOf(formatted, formatted.map { it.toUpperCase(Locale.ROOT) }, formatted.map { it.toLowerCase(Locale.ROOT) })
        return wordList.map { BrainKey(it, 0, prefix) }.filter { it.isValid }.toSet()
    }

    fun createFromMnemonicLowercase(seed: String, prefix: String = Graphene.KEY_SYMBOL.value): Set<BrainKey> {
        var formatted = seed.split(BRAIN_KEY_SPILT_PATTERN).filter { it.isNotBlank() }
        if (formatted.all { it.matches(CHINESE_WORDS_PATTERN) }) formatted = formatted.joinToString(EMPTY_SPACE).map { it.toString() }
        val wordList = setOf(formatted.map { it.toLowerCase(Locale.ROOT) })
        Log.d("KeyCreator", "createFromMnemonic: formatted $formatted")
        return wordList.map { BrainKey(it, 0, prefix) }.filter { it.isValid }.toSet()
    }

    fun createFromMnemonicUppercase(seed: String, prefix: String = Graphene.KEY_SYMBOL.value): Set<BrainKey> {
        var formatted = seed.split(BRAIN_KEY_SPILT_PATTERN).filter { it.isNotBlank() }
        if (formatted.all { it.matches(CHINESE_WORDS_PATTERN) }) formatted = formatted.joinToString(EMPTY_SPACE).map { it.toString() }
        val wordList = setOf(formatted.map { it.toUpperCase(Locale.ROOT) })
        Log.d("KeyCreator", "createFromMnemonic: formatted $formatted")
        return wordList.map { BrainKey(it, 0, prefix) }.filter { it.isValid }.toSet()
    }


}