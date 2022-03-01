package bitshareskit.security

import bitcoinkit.TimeEntropySource
import java.security.SecureRandom
import kotlin.math.absoluteValue

class NonceGenerator {

    companion object {

        val INSTANCE by lazy {
            NonceGenerator()
        }
        private const val DEFAULT_PSEUDO_RANDOM_NUMBER_GENERATOR = "SHA1PRNG"

        // TODO: 2022/2/19 replace with
        private val timeEntropySource: TimeEntropySource = TimeEntropySource()
    }

    private val randomGenerator = SecureRandom.getInstance(DEFAULT_PSEUDO_RANDOM_NUMBER_GENERATOR)

    fun generateNonce(): Long {
        randomGenerator.setSeed(timeEntropySource.entropy)
        return randomGenerator.nextLong().absoluteValue
    }

}