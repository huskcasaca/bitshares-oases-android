package bitcoinkit

import bitcoinkit.Utils.doubleDigest
import java.lang.StringBuilder
import java.math.BigInteger
import java.util.*

/**
 * A Hierarchical Deterministic key
 */
class HDKey : ECKey {
    /**
     * Return the chain code
     *
     * @return Chain code
     */
    /**
     * Chain code
     */
    val chainCode: ByteArray
    /**
     * Return the parent
     *
     * @return Parent or null if this is the root key
     */
    /**
     * HD key parent (null if root key)
     */
    val parent: HDKey?
    /**
     * Return the child number
     *
     * @return Child number
     */
    /**
     * Child number
     */
    val childNumber: Int
    /**
     * Check if the key is hardened
     *
     * @return TRUE if the key is hardened
     */
    /**
     * Hardened key
     */
    val isHardened: Boolean
    /**
     * Return the hierarchy depth
     *
     * @return Hierarchy depth (root key is depth 0)
     */
    /**
     * Depth (root key is depth 0)
     */
    val depth: Int
    /**
     * Return the parent fingerprint
     *
     * @return Parent fingerprint
     */
    /**
     * Parent fingerprint or 0 if root key
     */
    val parentFingerprint: Int

    /**
     * Create a new HD key from a private key
     *
     * @param privKey     Private key
     * @param chainCode   Chain code
     * @param parent      Parent or null if no parent
     * @param childNumber Child number (first child is 0)
     * @param isHardened  TRUE if the child is hardened
     */
    constructor(privKey: BigInteger, chainCode: ByteArray, parent: HDKey?, childNumber: Int, isHardened: Boolean) : super(privKey, ECKey.Companion.publicPointFromPrivate(privKey), true) {
        require(privKeyBytes!!.size <= 33) { "Private key is longer than 33 bytes" }
        require(chainCode.size == 32) { "Chain code is not 32 bytes" }
        check(pubKey.size == 33) { "Public key is not compressed" }
        this.chainCode = Arrays.copyOfRange(chainCode, 0, chainCode.size)
        this.parent = parent
        this.isHardened = isHardened
        this.childNumber = childNumber
        depth = if (parent != null) parent.depth + 1 else 0
        parentFingerprint = parent?.fingerprint ?: 0
    }

    /**
     * Create a new HD key from a public key.  The HD key will not have a private
     * key.
     *
     * @param pubKey      Public key
     * @param chainCode   Chain code
     * @param parent      Parent or null if no parent
     * @param childNumber Child number (first child is 0)
     * @param isHardened  TRUE if the child is hardened
     */
    constructor(pubKey: ByteArray, chainCode: ByteArray, parent: HDKey?, childNumber: Int, isHardened: Boolean) : super(pubKey) {
        require(pubKey.size == 33) { "Public key is not compressed" }
        require(chainCode.size == 32) { "Chain code is not 32 bytes" }
        this.chainCode = Arrays.copyOfRange(chainCode, 0, chainCode.size)
        this.parent = parent
        this.isHardened = isHardened
        this.childNumber = childNumber
        depth = if (parent != null) parent.depth + 1 else 0
        parentFingerprint = parent?.fingerprint ?: 0
    }

    /**
     * Return private key padded to 33 bytes
     *
     * @return Padded private key
     */
    val paddedPrivKeyBytes: ByteArray
        get() {
            val privKeyBytes = privKeyBytes
            val paddedBytes = ByteArray(33)
            System.arraycopy(privKeyBytes!!, 0, paddedBytes, 33 - privKeyBytes.size, privKeyBytes.size)
            return paddedBytes
        }//
    // The fingerprint is the first 32 bits of HASH160(pubKey)
    //
    /**
     * Return the public key fingerprint
     *
     * @return Fingerprint
     */
    val fingerprint: Int
        get() {
            //
            // The fingerprint is the first 32 bits of HASH160(pubKey)
            //
            val pubKeyHash = pubKeyHash
            return pubKeyHash!![0].toInt() and 255 shl 24 or (pubKeyHash[1].toInt() and 255 shl 16) or
                    (pubKeyHash[2].toInt() and 255 shl 8) or (pubKeyHash[3].toInt() and 255)
        }

    /**
     * Add the 4-byte checksum to the serialized key
     *
     * @param input Serialized key
     * @return Key plus checksum
     */
    private fun addChecksum(input: ByteArray): ByteArray {
        val inputLength = input.size
        val checksummed = ByteArray(inputLength + 4)
        System.arraycopy(input, 0, checksummed, 0, inputLength)
        val checksum: ByteArray = doubleDigest(input)
        System.arraycopy(checksum, 0, checksummed, inputLength, 4)
        return checksummed
    }

    /**
     * Get the path from the root key
     *
     * @return List of node numbers
     */
    val path: MutableList<Int>
        get() {
            val path: MutableList<Int>
            if (parent != null) {
                path = parent.path
                path.add(childNumber)
            } else {
                path = ArrayList()
            }
            return path
        }

    /**
     * Get string representation of this key
     *
     * @return Path string
     */
    override fun toString(): String {
        val sb = StringBuilder()
        return if (parent != null) {
            val parentPath = parent.toString()
            sb.append(parentPath).append("/").append(childNumber).append(if (isHardened) "'" else "")
            sb.toString()
        } else {
            "m"
        }
    }

    companion object {
        /**
         * Child is hardened
         */
        const val HARDENED_FLAG = -0x80000000
    }
}