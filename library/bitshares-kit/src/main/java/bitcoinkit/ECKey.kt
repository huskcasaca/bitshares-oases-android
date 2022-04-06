package bitcoinkit

import bitcoinkit.Utils.doubleDigest
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.DERSequenceGenerator
import org.bouncycastle.asn1.DLSequence
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.crypto.signers.HMacDSAKCalculator
import org.bouncycastle.math.ec.ECAlgorithms
import org.bouncycastle.math.ec.ECPoint
import org.bouncycastle.math.ec.FixedPointCombMultiplier
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve
import org.bouncycastle.util.encoders.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.*
import kotlin.experimental.and

/**
 * ECKey supports elliptic curve cryptographic operations using a public/private
 * key pair.  A private key is required to create a signature and a public key is
 * required to verify a signature.  The private key is always encrypted using AES
 * when it is serialized for storage on external media.
 */
open class ECKey @JvmOverloads constructor(
    pubKey: ByteArray?,
    /** Private key  */
    val privKey: BigInteger? = null, pub: ECPoint? = CURVE.curve.decodePoint(pubKey), compressed: Boolean = false
) {
    companion object {
        /** Elliptic curve parameters (secp256k1 curve)  */
        val CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1")
        val CURVE: ECDomainParameters = ECDomainParameters(CURVE_PARAMS.curve, CURVE_PARAMS.g, CURVE_PARAMS.n, CURVE_PARAMS.h)
        /** Half-curve order for generating canonical S  */
        val HALF_CURVE_ORDER: BigInteger = CURVE_PARAMS.n.shiftRight(1)

        /** Strong random number generator  */
        private val secureRandom = SecureRandom()

        /** Signed message header  */
        private const val BITCOIN_SIGNED_MESSAGE_HEADER = "Bitcoin Signed Message:\n"

        /**
         * Create the public key from the private key
         *
         * @param       privKey             Private key
         * @param       compressed          TRUE to generate a compressed public key
         * @return Public key
         */
        fun pubKeyFromPrivKey(privKey: BigInteger, compressed: Boolean): ByteArray {
            return publicPointFromPrivate(privKey).getEncoded(compressed)
        }

        fun fromPublicOnly(pub: ByteArray?): ECKey {
            return ECKey(pub, CURVE.curve.decodePoint(pub))
        }

        fun fromPrivate(privKeyBytes: ByteArray?): ECKey {
            return fromPrivate(BigInteger(1, privKeyBytes))
        }

        fun fromPrivate(privKeyBytes: ByteArray?, compressed: Boolean): ECKey {
            val privKey = BigInteger(1, privKeyBytes)
            val point = publicPointFromPrivate(privKey)
            return ECKey(privKey, point, compressed)
        }

        @JvmOverloads
        fun fromPrivate(privKey: BigInteger, compressed: Boolean = true): ECKey {
            val point = publicPointFromPrivate(privKey)
            return ECKey(null, privKey, point, compressed)
        }

        /**
         * Checks if the public key is canonical
         *
         * @param       pubKeyBytes         Public key
         * @return TRUE if the key is canonical
         */
        fun isPubKeyCanonical(pubKeyBytes: ByteArray): Boolean {
            if (pubKeyBytes.size < 33) return false
            if (pubKeyBytes[0] == 0x04.toByte()) {
                // Uncompressed pubkey
                if (pubKeyBytes.size != 65) return false
            } else if (pubKeyBytes[0] == 0x02.toByte() || pubKeyBytes[0] == 0x03.toByte()) {
                // Compressed pubkey
                if (pubKeyBytes.size != 33) return false
            } else return false
            return true
        }

        /**
         * Checks if the signature is DER-encoded
         *
         * @param       encodedSig          Encoded signature
         * @return TRUE if the signature is DER-encoded
         */
        fun isSignatureCanonical(encodedSig: ByteArray): Boolean {
            //
            // DER-encoding requires that there is only one representation for a given
            // encoding.  This means that no pad bytes are inserted for numeric values.
            //
            // An ASN.1 sequence is identified by 0x30 and each primitive by a type field.
            // An integer is identified as 0x02.  Each field type is followed by a field length.
            // For valid R and S values, the length is a single byte since R and S are both
            // 32-byte or 33-byte values (a leading zero byte is added to ensure a positive
            // value if the sign bit would otherwise bet set).
            //
            // Bitcoin appends that hash type to the end of the DER-encoded signature.  We require
            // this to be a single byte for a canonical signature.
            //
            // The length is encoded in the lower 7 bits for lengths between 0 and 127 and the upper bit is 0.
            // Longer length have the upper bit set to 1 and the lower 7 bits contain the number of bytes
            // in the length.
            //

            //
            // An ASN.1 sequence is 0x30 followed by the length
            //
            if (encodedSig.size < 2 || encodedSig[0] != 0x30.toByte() || encodedSig[1] and 0x80.toByte() != 0.toByte()) return false
            //
            // Get length of sequence
            //
            val length = (encodedSig[1].toInt() and 0x7f) + 2
            var offset = 2
            //
            // Check R
            //
            if (offset + 2 > length || encodedSig[offset] != 0x02.toByte() || encodedSig[offset + 1] and 0x80.toByte() != 0.toByte()) return false
            val rLength = encodedSig[offset + 1].toInt() and 0x7f
            if (offset + rLength + 2 > length) return false
            if (encodedSig[offset + 2] == 0x00.toByte() && encodedSig[offset + 3] and 0x80.toByte() == 0.toByte()) return false
            offset += rLength + 2
            //
            // Check S
            //
            if (offset + 2 > length || encodedSig[offset] != 0x02.toByte() || encodedSig[offset + 1] and 0x80.toByte() != 0.toByte()) return false
            val sLength = encodedSig[offset + 1].toInt() and 0x7f
            if (offset + sLength + 2 > length) return false
            if (encodedSig[offset + 2] == 0x00.toByte() && encodedSig[offset + 3] and 0x80.toByte() == 0.toByte()) return false
            offset += sLength + 2
            //
            // There must be a single byte appended to the signature
            //
            return offset == encodedSig.size - 1
        }

        /**
         *
         * Given the components of a signature and a selector value, recover and return the public key
         * that generated the signature according to the algorithm in SEC1v2 section 4.1.6.
         *
         *
         * The recID is an index from 0 to 3 which indicates which of the 4 possible keys is the correct one.
         * Because the key recovery operation yields multiple potential keys, the correct key must either be
         * stored alongside the signature, or you must be willing to try each recId in turn until you find one
         * that outputs the key you are expecting.
         *
         *
         * If this method returns null, it means recovery was not possible and recID should be iterated.
         *
         *
         * Given the above two points, a correct usage of this method is inside a for loop from 0 to 3, and if the
         * output is null OR a key that is not the one you expect, you try again with the next recID.
         *
         * @param       recID               Which possible key to recover.
         * @param       sig                 R and S components of the signature
         * @param       e                   The double SHA-256 hash of the original message
         * @param       compressed          Whether or not the original public key was compressed
         * @return An ECKey containing only the public part, or null if recovery wasn't possible
         */
        fun recoverFromSignature(recID: Int, sig: ECDSASignature, e: BigInteger?, compressed: Boolean): ECKey? {
            val n = CURVE.n
            val i = BigInteger.valueOf(recID.toLong() / 2)
            val x = sig.r!!.add(i.multiply(n))
            //
            //   Convert the integer x to an octet string X of length mlen using the conversion routine
            //        specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen = ⌈m/8⌉.
            //   Convert the octet string (16 set binary digits)||X to an elliptic curve point R using the
            //        conversion routine specified in Section 2.3.4. If this conversion routine outputs 'invalid', then
            //        do another iteration.
            //
            // More concisely, what these points mean is to use X as a compressed public key.
            //
            val curve = CURVE.curve as SecP256K1Curve
            val prime = curve.q
            if (x.compareTo(prime) >= 0) {
                return null
            }
            //
            // Compressed keys require you to know an extra bit of data about the y-coordinate as
            // there are two possibilities.  So it's encoded in the recID.
            //
            val R = decompressKey(x, recID and 1 == 1)
            if (!R.multiply(n).isInfinity) return null
            //
            //   For k from 1 to 2 do the following.   (loop is outside this function via iterating recId)
            //     Compute a candidate public key as:
            //       Q = mi(r) * (sR - eG)
            //
            // Where mi(x) is the modular multiplicative inverse. We transform this into the following:
            //               Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
            // Where -e is the modular additive inverse of e, that is z such that z + e = 0 (mod n).
            // In the above equation, ** is point multiplication and + is point addition (the EC group operator).
            //
            // We can find the additive inverse by subtracting e from zero then taking the mod. For example the additive
            // inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and -3 mod 11 = 8.
            //
            val eInv = BigInteger.ZERO.subtract(e).mod(n)
            val rInv = sig.r!!.modInverse(n)
            val srInv = rInv.multiply(sig.s).mod(n)
            val eInvrInv = rInv.multiply(eInv).mod(n)
            val q = ECAlgorithms.sumOfTwoMultiplies(CURVE.g, eInvrInv, R, srInv)
            return ECKey(q.getEncoded(compressed), q)
        }

        /**
         * Decompress a compressed public key (x coordinate and low-bit of y-coordinate).
         *
         * @param       xBN                 X-coordinate
         * @param       yBit                Sign of Y-coordinate
         * @return Uncompressed public key
         */
        private fun decompressKey(xBN: BigInteger, yBit: Boolean): ECPoint {
            val curve = CURVE.curve as SecP256K1Curve
            val x = curve.fromBigInteger(xBN)
            val alpha = x.multiply(x.square().add(curve.a)).add(curve.b)
            val beta = alpha.sqrt() ?: throw IllegalArgumentException("Invalid point compression")
            val ecPoint: ECPoint
            val nBeta = beta.toBigInteger()
            ecPoint = if (nBeta.testBit(0) == yBit) {
                curve.createPoint(x.toBigInteger(), nBeta)
            } else {
                val y = curve.fromBigInteger(curve.q.subtract(nBeta))
                curve.createPoint(x.toBigInteger(), y.toBigInteger())
            }
            return ecPoint
        }
        /**
         * Get the public key ECPoint from the private key
         *
         * @param       privKey             Private key
         * @return Public key ECPoint
         */
        //    static ECPoint publicPointFromPrivate(BigInteger privKey) {
        //        BigInteger adjKey;
        //        if (privKey.bitLength() > CURVE.getN().bitLength()) {
        //            adjKey = privKey.mod(CURVE.getN());
        //        } else {
        //            adjKey = privKey;
        //        }
        //        return CURVE.getG().multiply(adjKey);
        //    }
        /**
         * Returns public key bytes from the given private key. To convert a byte array into a BigInteger, use <tt>
         * new BigInteger(1, bytes);</tt>
         */
        fun publicKeyFromPrivate(privKey: BigInteger, compressed: Boolean): ByteArray {
            val point = publicPointFromPrivate(privKey)
            return point.getEncoded(compressed)
        }

        /**
         * Returns public key point from the given private key. To convert a byte array into a BigInteger, use <tt>
         * new BigInteger(1, bytes);</tt>
         */
        fun publicPointFromPrivate(privKey: BigInteger): ECPoint {
            /*
         * FIXME: FixedPointCombMultiplier currently doesn't support scalars longer than the group order,
         * but that could change in future versions.
         */
            var privKey = privKey
            if (privKey.bitLength() > CURVE.n.bitLength()) {
                privKey = privKey.mod(CURVE.n)
            }
            return FixedPointCombMultiplier().multiply(CURVE.g, privKey)
        }

        private fun getPointWithCompression(point: ECPoint, compressed: Boolean): ECPoint {
            var point = point
            point = point.normalize()
            val x = point.affineXCoord.toBigInteger()
            val y = point.affineYCoord.toBigInteger()
            return CURVE.curve.createPoint(x, y)
        }

        fun fromBytes(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Int {
            return b1.toInt() shl 24 or (b2.toInt() and 0xFF shl 16) or (b3.toInt() and 0xFF shl 8) or (b4.toInt() and 0xFF)
        }

    }
    /**
     * Returns the key label
     *
     * @return Key label
     */
    /**
     * Sets the key label
     *
     * @param       label               Key label
     */
    /** Key label  */
    var label = ""
    /**
     * Returns the public key (as used in transaction scriptSigs).  A compressed
     * public key is 33 bytes and starts with '02' or '03' while an uncompressed
     * public key is 65 bytes and starts with '04'.
     *
     * @return Public key
     */
    /** Public key  */
    val pubKey: ByteArray
    /**
     * Returns the public key hash as used in addresses.  The hash is 20 bytes.
     *
     * @return Public key hash
     */
    /** Public key hash  */
    val pubKeyHash: ByteArray by lazy { Utils.sha256Hash160(pubKey) }
    /**
     * Returns the public key checksum as used in addresses.  The hash is 4 bytes.
     *
     * @return Public key hash
     */
    /** Public key checksum  */
    val pubKeyChecksum: ByteArray by lazy { Arrays.copyOfRange(Utils.ripemd160(pubKey!!), 0, 4) }

    /** P2SH-P2WPKH script hash  */
//    private val scriptHash: ByteArray
    /**
     * Returns the private key
     *
     * @return Private key or null if there is no private key
     */
    /**
     * Returns the key creation time
     *
     * @return Key creation time (seconds)
     */
    /**
     * Sets the key creation time
     *
     * @param       creationTimeSeconds        Key creation time (seconds)
     */
    /** Key creation time (seconds)  */
    var creationTimeSeconds: Long
    /**
     * Checks if the public key is compressed
     *
     * @return TRUE if the public key is compressed
     */
    /** Compressed public key  */
    var isCompressed = false
    protected val pub: LazyECPoint

    constructor(pubKey: ByteArray?, pub: ECPoint?) : this(pubKey, null, pub, false) {}

    /**
     * Creates an ECKey public/private key pair using the supplied private key.  The
     * 'compressed' parameter determines the type of public key created.
     *
     * @param       privKey             Private key
     * @param       compressed          TRUE to create a compressed public key
     */
    constructor(privKey: BigInteger?, pub: ECPoint?, compressed: Boolean) : this(null, privKey, pub, compressed) {}

    val pubKeyPoint: ECPoint
        get() = pub.get()

    /**
     * Return the private key bytes
     *
     * @return Private key bytes or null if there is no private key
     */
    val privKeyBytes: ByteArray?
        get() = if (privKey != null) Utils.bigIntegerToBytes(privKey, 32) else null

    /**
     * Checks if there is a private key
     *
     * @return TRUE if there is a private key
     */
    fun hasPrivKey(): Boolean {
        return privKey != null
    }

    /**
     * Creates a signature for the supplied contents using the private key
     *
     * @param   input                   SHA256 hash of contents to be
     * @return  signed ECDSA signature
     * @throws  ECException             Unable to create signature
     */
    @Throws(ECException::class)
    fun sign(input: ByteArray): ECDSASignature {
        checkNotNull(privKey) { "No private key available" }
        //
        // Create the signature
        //
        val sigs: Array<BigInteger> = try {
            val signer = ECDSACanonicalSigner(HMacDSAKCalculator(SHA256Digest()))
            val privKeyParams = ECPrivateKeyParameters(privKey, CURVE)
            signer.init(true, privKeyParams)
            signer.generateSignature(input)
        } catch (exc: RuntimeException) {
            throw ECException("Exception while creating signature", exc)
        }
        //
        // Create a canonical signature by adjusting the S component to be less than or equal to
        // half the curve order.
        //
        if (sigs[1].compareTo(HALF_CURVE_ORDER) > 0) sigs[1] = CURVE.n.subtract(sigs[1])
        return ECDSASignature(sigs[0], sigs[1])
    }

    /**
     * Creates a signature for the supplied contents using the private key
     *
     * @param   contents                Contents to be signed
     * @return  ECDSA signature
     * @throws  ECException             Unable to create signature
     */
    @Throws(ECException::class)
    fun createECDSASignature(contents: ByteArray): ECDSASignature {
        checkNotNull(privKey) { "No private key available" }
        //
        // Get the double SHA-256 hash of the signed contents
        //
        val contentsHash: ByteArray = doubleDigest(contents)
        //
        // Create the signature
        //
        val sigs: Array<BigInteger>
        sigs = try {
            val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
            val privKeyParams = ECPrivateKeyParameters(privKey, CURVE)
            signer.init(true, privKeyParams)
            signer.generateSignature(contentsHash)
        } catch (exc: RuntimeException) {
            throw ECException("Exception while creating signature", exc)
        }
        //
        // Create a canonical signature by adjusting the S component to be less than or equal to
        // half the curve order.
        //
        if (sigs[1].compareTo(HALF_CURVE_ORDER) > 0) sigs[1] = CURVE.n.subtract(sigs[1])
        return ECDSASignature(sigs[0], sigs[1])
    }

    @Throws(ECException::class)
    fun createSignature(contents: ByteArray): ByteArray? {
        return createECDSASignature(contents).encodeToDER()
    }

    /**
     * Signs a message using the private key
     *
     * @param   message             Message to be signed
     * @return  Base64-encoded signature string
     * @throws  ECException         Unable to sign the message
     */
    @Throws(ECException::class)
    fun signMessage(message: String): String {
        val encodedSignature: String
        checkNotNull(privKey) { "No private key available" }
        try {
            //
            // Format the message for signing
            //
            var contents: ByteArray
            ByteArrayOutputStream(message.length * 2).use { outStream ->
                val headerBytes = BITCOIN_SIGNED_MESSAGE_HEADER.toByteArray(StandardCharsets.UTF_8)
                outStream.write(Utils.encode(headerBytes.size.toLong()))
                outStream.write(headerBytes)
                val messageBytes = message.toByteArray(StandardCharsets.UTF_8)
                outStream.write(Utils.encode(messageBytes.size.toLong()))
                outStream.write(messageBytes)
                contents = outStream.toByteArray()
            }
            //
            // Create the signature
            //
            val sig = createECDSASignature(contents)
            //
            // Get the RecID used to recover the public key from the signature
            //
            val e: BigInteger = BigInteger(1, doubleDigest(contents))
            var recID = -1
            for (i in 0..3) {
                val k = recoverFromSignature(i, sig, e, isCompressed)
                if (k != null && Arrays.equals(k.pubKey, pubKey)) {
                    recID = i
                    break
                }
            }
            if (recID == -1) throw ECException("Unable to recover public key from signature")
            //
            // The message signature consists of a header byte followed by the R and S values
            //
            val headerByte = recID + 27 + if (isCompressed) 4 else 0
            val sigData = ByteArray(65)
            sigData[0] = headerByte.toByte()
            System.arraycopy(Utils.bigIntegerToBytes(sig.r, 32)!!, 0, sigData, 1, 32)
            System.arraycopy(Utils.bigIntegerToBytes(sig.s, 32)!!, 0, sigData, 33, 32)
            //
            // Create a Base-64 encoded string for the message signature
            //
            encodedSignature = String(Base64.encode(sigData), StandardCharsets.UTF_8)
        } catch (exc: IOException) {
            throw IllegalStateException("Unexpected IOException", exc)
        }
        return encodedSignature
    }

    /**
     * Signs a hashed bytes using the private key
     *
     * @param   hashed              Hashed bytes to be signed
     * @return  65 byte length signed signature
     * @throws  ECException         Unable to sign the hashed bytes
     */
    @Throws(ECException::class)
    fun signOnly(hashed: ByteArray): ByteArray {
        val signed = sign(hashed)
        var recId = -1
        for (i in 0..3) {
            val k = recoverFromSignature(i, signed, BigInteger(1, hashed), isCompressed)
            if (k != null && Arrays.equals(k.pubKey, pubKey)) {
                recId = i
                break
            }
        }
        if (recId == -1) throw RuntimeException("Could not construct a recoverable key. This should never happen.")
        val headerByte = recId + 27 + if (isCompressed) 4 else 0
        val sigData = ByteArray(65)
        sigData[0] = headerByte.toByte()
        System.arraycopy(Utils.bigIntegerToBytes(signed.r, 32)!!, 0, sigData, 1, 32)
        System.arraycopy(Utils.bigIntegerToBytes(signed.s, 32)!!, 0, sigData, 33, 32)
        return sigData
    }

    /**
     * Checks if two objects are equal
     *
     * @param   obj             The object to check
     * @return  TRUE if the object is equal
     */
    override fun equals(obj: Any?): Boolean {
        return obj is ECKey && Arrays.equals(pubKey, obj.pubKey)
    }

    /**
     * Returns the hash code for this object
     *
     * @return Hash code
     */
    //    @Override
    //    public int hashCode() {
    //        return Arrays.hashCode(pubKey);
    //    }
    // FIXME: 2021/8/18 fix hashcode
    override fun hashCode(): Int {
        // Public keys are random already so we can just use a part of them as the hashcode. Read from the start to
        // avoid picking up the type code (compressed vs uncompressed) which is tacked on the end.
        val bits = pubKey
        return fromBytes(bits[0], bits[1], bits[2], bits[3])
    }

    /**
     * ECDSASignature is an elliptic curve digital signature consisting of the
     * R and S values.
     */
    class ECDSASignature {
        /**
         * Returns the R value
         *
         * @return R component
         */
        /**
         * R and S components of the digital signature
         */
        var r: BigInteger? = null
            private set

        /**
         * Returns the S value
         *
         * @return S component
         */
        var s: BigInteger? = null
            private set

        /**
         * Creates a digital signature from the R and S values
         *
         * @param r R value
         * @param s S value
         */
        constructor(r: BigInteger?, s: BigInteger?) {
            this.r = r
            this.s = s
        }

        /**
         * Creates a digital signature from the DER-encoded values
         *
         * @param encodedStream DER-encoded value
         * @throws ECException Unable to decode the stream
         */
        constructor(encodedStream: ByteArray?) {
            try {
                ASN1InputStream(encodedStream).use { decoder ->
                    val seq = decoder.readObject() as DLSequence
                    r = (seq.getObjectAt(0) as ASN1Integer).positiveValue
                    s = (seq.getObjectAt(1) as ASN1Integer).positiveValue
                }
            } catch (exc: ClassCastException) {
                throw ECException("Unable to decode signature", exc)
            } catch (exc: IOException) {
                throw ECException("Unable to decode signature", exc)
            }
        }

        /**
         * Encodes R and S as a DER-encoded byte stream
         *
         * @return DER-encoded byte stream
         */
        fun encodeToDER(): ByteArray? {
            var encodedBytes: ByteArray? = null
            try {
                ByteArrayOutputStream(72).use { outStream ->
                    val seq = DERSequenceGenerator(outStream)
                    seq.addObject(ASN1Integer(r))
                    seq.addObject(ASN1Integer(s))
                    seq.close()
                    encodedBytes = outStream.toByteArray()
                }
            } catch (exc: IOException) {
                throw IllegalStateException("Unexpected IOException", exc)
            }
            return encodedBytes
        }

        /**
         * Returns true if the S component is "low", that means it is below [ECKey.HALF_CURVE_ORDER]. See [BIP62](https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#Low_S_values_in_signatures).
         */
        val isCanonical: Boolean
            get() = s!!.compareTo(HALF_CURVE_ORDER) <= 0

        /**
         * Will automatically adjust the S component to be less than or equal to half the curve order, if necessary.
         * This is required because for every signature (r,s) the signature (r, -s (mod N)) is a valid signature of
         * the same message. However, we dislike the ability to modify the bits of a Bitcoin transaction after it's
         * been signed, as that violates various assumed invariants. Thus in future only one of those forms will be
         * considered legal and the other will be banned.
         */
        fun toCanonicalised(): ECDSASignature {
            return if (!isCanonical) {
                // The order of the curve is the number of valid points that exist on that curve. If S is in the upper
                // half of the number of valid points, then bring it back to the lower half. Otherwise, imagine that
                //    N = 10
                //    s = 8, so (-8 % 10 == 2) thus both (r, 8) and (r, 2) are valid solutions.
                //    10 - 8 == 2, giving us always the latter solution, which is canonical.
                ECDSASignature(r, CURVE.n.subtract(s))
            } else {
                this
            }
        }
    }
    /**
     * Creates an ECKey with the supplied public/private key pair.  The private key may be
     * null if you only want to use this ECKey to verify signatures.  The public key will
     * be generated from the private key if it is not provided (the 'compressed' parameter
     * determines the type of public key created)
     * @param       pubKey              Public key or null
     * @param       privKey             Private key or null
     * @param pub
     * @param       compressed          TRUE to create a compressed public key
     */
    /**
     * Creates an ECKey with just a public key
     *
     * @param       pubKey              Public key
     */
    init {
        this.pub = LazyECPoint(pub)
        if (pubKey != null) {
            this.pubKey = Arrays.copyOfRange(pubKey, 0, pubKey.size)
            isCompressed = pubKey.size == 33
        } else if (privKey != null) {
            this.pubKey = pubKeyFromPrivKey(privKey, compressed)
            isCompressed = compressed
        } else {
            throw IllegalArgumentException("You must provide at least a private key or a public key")
        }
        creationTimeSeconds = System.currentTimeMillis() / 1000
    }
}