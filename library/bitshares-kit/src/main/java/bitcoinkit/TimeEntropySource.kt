package bitcoinkit

import org.bouncycastle.crypto.prng.EntropySource
import java.nio.ByteBuffer

class TimeEntropySource : EntropySource {
    override fun isPredictionResistant(): Boolean {
        return false
    }

    override fun getEntropy(): ByteArray {
        val timeBuffer = ByteBuffer.allocate(entropySize())
        timeBuffer.clear()
        timeBuffer.putLong(System.currentTimeMillis())
        timeBuffer.putLong(System.nanoTime())
        timeBuffer.flip()
        return timeBuffer.array()
    }

    override fun entropySize(): Int {
        return java.lang.Long.SIZE / java.lang.Byte.SIZE * 2
    }
}