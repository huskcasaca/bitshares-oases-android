package bitcoinkit

import org.bouncycastle.math.ec.ECPoint
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*

/**
 * Hierarchical Deterministic key derivation (BIP 32)
 */
object HDKeyDerivation {
    /**
     * Generate a root key from the given seed.  The seed must be at least 128 bits.
     *
     * @param seed HD seed
     * @return Root key
     * @throws HDDerivationException Generated master key is invalid
     */
    @Throws(HDDerivationException::class)
    fun createRootKey(seed: ByteArray): HDKey {
        require(seed.size >= 16) { "Seed must be at least 128 bits" }
        //
        // From BIP 32:
        // - Generate a seed byte sequence S of a chosen length (between 128 and 512 bits)
        // - Calculate I = HMAC-SHA512(Key = "Bitcoin seed", Data = S)
        // - Split I into two 32-byte sequences, IL and IR.
        // - Use parse256(IL) as master secret key, and IR as master chain code.
        //   In case IL is 0 or ≥n, the master key is invalid.
        //
        val i = Utils.hmacSha512("Bitcoin seed".toByteArray(), seed)
        val il = Arrays.copyOfRange(i, 0, 32)
        val ir = Arrays.copyOfRange(i, 32, 64)
        val privKey = BigInteger(1, il)
        if (privKey.signum() == 0) throw HDDerivationException("Generated master private key is zero")
        if (privKey.compareTo(ECKey.Companion.CURVE!!.getN()) >= 0) throw HDDerivationException("Generated master private key is not less than N")
        return HDKey(privKey, ir, null, 0, false)
    }

    /**
     *
     * Derive a child key from the specified parent.
     *
     *
     *
     * The parent must have a private key in order to derive a private/public key pair.
     * If the parent does not have a private key, only the public key can be derived.
     * In addition, a hardened key cannot be derived from a public key since the algorithm requires
     * the parent private key.
     *
     *
     *
     * It is possible for key derivation to fail for a child number because the generated
     * key is not valid.  If this happens, the application should generate a key using
     * a different child number.
     *
     * @param parent      Parent key
     * @param childNumber Child number
     * @param hardened    TRUE to create a hardened key
     * @return Derived key
     * @throws HDDerivationException Unable to derive key
     */
    @Throws(HDDerivationException::class)
    fun deriveChildKey(parent: HDKey, childNumber: Int, hardened: Boolean): HDKey {
        val derivedKey: HDKey
        require(childNumber and HDKey.Companion.HARDENED_FLAG == 0) { "Hardened flag must not be set in child number" }
        derivedKey = if (parent.privKey == null) {
            check(!hardened) { "Hardened key requires parent private key" }
            derivePublicKey(parent, childNumber)
        } else {
            derivePrivateKey(parent, childNumber, hardened)
        }
        return derivedKey
    }

    /**
     * Derive a child key from a private key
     *
     * @param parent      Parent key
     * @param childNumber Child number
     * @param hardened    TRUE to create a hardened key
     * @return Derived key
     * @throws HDDerivationException Unable to derive key
     */
    @Throws(HDDerivationException::class)
    private fun derivePrivateKey(parent: HDKey, childNumber: Int, hardened: Boolean): HDKey {
        val parentPubKey = parent.pubKey
        check(parentPubKey!!.size == 33) { "Parent public key is not 33 bytes" }
        //
        // From BIP 32:
        // - Check whether i ≥ 231 (whether the child is a hardened key).
        // - If so (hardened child): let I = HMAC-SHA512(Key = cpar, Data = 0x00 || ser256(kpar) || ser32(i)).
        //   (Note: The 0x00 pads the private key to make it 33 bytes long.)
        // - If not (normal child): let I = HMAC-SHA512(Key = cpar, Data = serP(point(kpar)) || ser32(i)).
        // - Split I into two 32-byte sequences, IL and IR.
        // - The returned child key ki is parse256(IL) + kpar (mod n).
        // - The returned chain code ci is IR.
        //
        // In case parse256(IL) ≥ n or ki = 0, the resulting key is invalid,
        // and one should proceed with the next value for i.
        //
        val dataBuffer = ByteBuffer.allocate(37)
        if (hardened) {
            dataBuffer.put(parent.paddedPrivKeyBytes)
                .putInt(childNumber or HDKey.Companion.HARDENED_FLAG)
        } else {
            dataBuffer.put(parentPubKey)
                .putInt(childNumber)
        }
        val i = Utils.hmacSha512(parent.chainCode, dataBuffer.array())
        val il = Arrays.copyOfRange(i, 0, 32)
        val ir = Arrays.copyOfRange(i, 32, 64)
        val ilInt = BigInteger(1, il)
        if (ilInt.compareTo(ECKey.Companion.CURVE!!.getN()) >= 0) throw HDDerivationException("Derived private key is not less than N")
        val ki = parent.privKey!!.add(ilInt).mod(ECKey.Companion.CURVE!!.getN())
        if (ki.signum() == 0) throw HDDerivationException("Derived private key is zero")
        return HDKey(ki, ir, parent, childNumber, hardened)
    }

    /**
     * Derive a child key from a public key
     *
     * @param parent      Parent key
     * @param childNumber Child number
     * @return Derived key
     * @throws HDDerivationException Unable to derive key
     */
    @Throws(HDDerivationException::class)
    private fun derivePublicKey(parent: HDKey, childNumber: Int): HDKey {
        //
        // - If not (normal child): let I = HMAC-SHA512(Key = cpar, Data = serP(Kpar) || ser32(i)).
        // - Split I into two 32-byte sequences, IL and IR.
        // - The returned child key Ki is point(parse256(IL)) + Kpar.
        // - The returned chain code ci is IR.
        //
        // In case parse256(IL) ≥ n or Ki is the point at infinity, the resulting key is invalid,
        // and one should proceed with the next value for i.
        //
        val dataBuffer = ByteBuffer.allocate(37)
        dataBuffer.put(parent.pubKey).putInt(childNumber)
        val i = Utils.hmacSha512(parent.chainCode, dataBuffer.array())
        val il = Arrays.copyOfRange(i, 0, 32)
        val ir = Arrays.copyOfRange(i, 32, 64)
        val ilInt = BigInteger(1, il)
        if (ilInt.compareTo(ECKey.Companion.CURVE!!.getN()) >= 0) throw HDDerivationException("Derived private key is not less than N")
        val pubKeyPoint: ECPoint = ECKey.Companion.CURVE!!.getCurve().decodePoint(parent.pubKey)
        val Ki: ECPoint = ECKey.Companion.publicPointFromPrivate(ilInt).add(pubKeyPoint)
        if (Ki.equals(ECKey.Companion.CURVE!!.getCurve().getInfinity())) throw HDDerivationException("Derived public key equals infinity")
        return HDKey(Ki.getEncoded(true), ir, parent, childNumber, false)
    }
}