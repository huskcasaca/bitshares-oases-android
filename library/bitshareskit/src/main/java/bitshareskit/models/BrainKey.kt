package bitshareskit.models

import bitshareskit.extensions.BLANK_SPACE
import bitshareskit.extensions.sha256
import bitshareskit.extensions.sha512
import kotlin.random.Random

class BrainKey(
    val words: List<String>,
    val sequence: Int,
    prefix: String = PublicKey.BITSHARES_MAINNET_PREFIX
): PrivateKey(createBytesFromSeed(words, sequence), KeyType.MNEMONIC, prefix) {
    companion object {

        private const val BRAINKEY_WORD_COUNT = 12
        private val BRAINKEY_SEED_RANGE = 10..16

        private fun createBytesFromSeed(words: List<String>, sequence: Int,): ByteArray? {
            return if (words.size in BRAINKEY_SEED_RANGE) (words + sequence).joinToString(BLANK_SPACE).sha512().sha256() else null
        }

        fun suggest(dict: List<String>, prefix: String): BrainKey = suggest(dict, BRAINKEY_WORD_COUNT, prefix)

        fun suggest(dict: List<String>, size: Int, prefix: String): BrainKey = BrainKey(List(size) { dict[Random.nextInt(dict.size - 1)] }, 0, prefix)

    }




}