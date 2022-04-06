package bitcoinkit

import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.CryptoServicesRegistrar
import org.bouncycastle.crypto.DSAExt
import org.bouncycastle.crypto.params.ECKeyParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.crypto.params.ParametersWithRandom
import org.bouncycastle.crypto.signers.DSAKCalculator
import org.bouncycastle.crypto.signers.RandomDSAKCalculator
import org.bouncycastle.math.ec.*
import java.math.BigInteger
import java.security.SecureRandom
import kotlin.experimental.and

/**
 * EC-DSA as described in X9.62
 */
class ECDSACanonicalSigner : ECConstants, DSAExt {
    private val kCalculator: DSAKCalculator
    private var key: ECKeyParameters? = null
    private var random: SecureRandom? = null

    /**
     * Default configuration, random K values.
     */
    constructor() {
        kCalculator = RandomDSAKCalculator()
    }

    /**
     * Configuration with an alternate, possibly deterministic calculator of K.
     *
     * @param kCalculator a K value calculator.
     */
    constructor(kCalculator: DSAKCalculator) {
        this.kCalculator = kCalculator
    }

    override fun init(forSigning: Boolean, param: CipherParameters) {
        var providedRandom: SecureRandom? = null
        if (forSigning) {
            if (param is ParametersWithRandom) {
                val rParam = param
                key = rParam.parameters as ECPrivateKeyParameters
                providedRandom = rParam.random
            } else {
                key = param as ECPrivateKeyParameters
            }
        } else {
            key = param as ECPublicKeyParameters
        }
        random = initSecureRandom(forSigning && !kCalculator.isDeterministic, providedRandom)
    }

    override fun getOrder(): BigInteger {
        return key!!.parameters.n
    }
    // 5.3 pg 28
    /**
     * generate a signature for the given message using the key we were initialised with.
     * For conventional DSA the message should be a SHA-1 hash of the message of interest.
     *
     * @param message the message that will be verified later.
     */
    override fun generateSignature(message: ByteArray): Array<BigInteger> {
        val ec = key!!.parameters
        val n = ec.n
        val e = calculateE(n, message)
        val d = (key as ECPrivateKeyParameters?)!!.d
        if (kCalculator.isDeterministic) {
            kCalculator.init(n, d, message)
        } else {
            kCalculator.init(n, random)
        }
        var r: BigInteger
        var s: BigInteger
        var rb: ByteArray?
        val basePointMultiplier = createBasePointMultiplier()
        do  // generate s
        {
            var k: BigInteger
            do  // generate r
            {
                k = kCalculator.nextK()
                val p = basePointMultiplier.multiply(ec.g, k).normalize()
                r = p.affineXCoord.toBigInteger().mod(n)
                rb = Utils.bigIntegerToBytes(r, 32)
            } while (r == ECConstants.ZERO || rb!![0] and 0x80.toByte() != 0.toByte() || rb[31] and 0x80.toByte() != 0.toByte() || rb[31] == 0.toByte())
            s = k.modInverse(n).multiply(e.add(d.multiply(r))).mod(n)
        } while (s == ECConstants.ZERO)
        return arrayOf(r, s)
    }

    /**
     * return true if the value r and s represent a DSA signature for the passed in message
     * (for standard DSA the message should be a SHA-1 hash of the real message to be verified).
     */
    override fun verifySignature(message: ByteArray, r: BigInteger, s: BigInteger): Boolean {
        var r = r
        val ec = key!!.parameters
        val n = ec.n
        val e = calculateE(n, message)

        // r in the range [1,n-1]
        if (r < ECConstants.ONE || r >= n) {
            return false
        }

        // s in the range [1,n-1]
        if (s < ECConstants.ONE || s >= n) {
            return false
        }
        val c = s.modInverse(n)
        val u1 = e.multiply(c).mod(n)
        val u2 = r.multiply(c).mod(n)
        val G = ec.g
        val Q = (key as ECPublicKeyParameters?)!!.q
        val point = ECAlgorithms.sumOfTwoMultiplies(G, u1, Q, u2)

        // components must be bogus.
        if (point.isInfinity) {
            return false
        }

        /*
         * If possible, avoid normalizing the point (to save a modular inversion in the curve field).
         *
         * There are ~cofactor elements of the curve field that reduce (modulo the group order) to 'r'.
         * If the cofactor is known and small, we generate those possible field values and project each
         * of them to the same "denominator" (depending on the particular projective coordinates in use)
         * as the calculated point.X. If any of the projected values matches point.X, then we have:
         *     (point.X / Denominator mod p) mod n == r
         * as required, and verification succeeds.
         *
         * Based on an original idea by Gregory Maxwell (https://github.com/gmaxwell), as implemented in
         * the libsecp256k1 project (https://github.com/bitcoin/secp256k1).
         */
        val curve = point.curve
        if (curve != null) {
            val cofactor = curve.cofactor
            if (cofactor != null && cofactor <= ECConstants.EIGHT) {
                val D = getDenominator(curve.coordinateSystem, point)
                if (D != null && !D.isZero) {
                    val X = point.xCoord
                    while (curve.isValidFieldElement(r)) {
                        val R = curve.fromBigInteger(r).multiply(D)
                        if (R == X) {
                            return true
                        }
                        r = r.add(n)
                    }
                    return false
                }
            }
        }
        val v = point.normalize().affineXCoord.toBigInteger().mod(n)
        return v == r
    }

    protected fun calculateE(n: BigInteger, message: ByteArray): BigInteger {
        val log2n = n.bitLength()
        val messageBitLength = message.size * 8
        var e = BigInteger(1, message)
        if (log2n < messageBitLength) {
            e = e.shiftRight(messageBitLength - log2n)
        }
        return e
    }

    protected fun createBasePointMultiplier(): ECMultiplier {
        return FixedPointCombMultiplier()
    }

    protected fun getDenominator(coordinateSystem: Int, p: ECPoint): ECFieldElement? {
        return when (coordinateSystem) {
            ECCurve.COORD_HOMOGENEOUS, ECCurve.COORD_LAMBDA_PROJECTIVE, ECCurve.COORD_SKEWED -> p.getZCoord(0)
            ECCurve.COORD_JACOBIAN, ECCurve.COORD_JACOBIAN_CHUDNOVSKY, ECCurve.COORD_JACOBIAN_MODIFIED -> p.getZCoord(0).square()
            else -> null
        }
    }

    protected fun initSecureRandom(needed: Boolean, provided: SecureRandom?): SecureRandom? {
        return if (!needed) null else provided ?: CryptoServicesRegistrar.getSecureRandom()
    }
}