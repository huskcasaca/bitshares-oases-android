package bitcoinkit

import org.bouncycastle.math.ec.ECCurve
import org.bouncycastle.math.ec.ECFieldElement
import org.bouncycastle.math.ec.ECPoint
import java.math.BigInteger
import java.util.*

/**
 * A wrapper around ECPoint that delays decoding of the point for as long as possible. This is useful because point
 * encode/decode in Bouncy Castle is quite slow especially on Dalvik, as it often involves decompression/recompression.
 */
class LazyECPoint {
    // If curve is set, bits is also set. If curve is unset, point is set and bits is unset. Point can be set along
    // with curve and bits when the cached form has been accessed and thus must have been converted.
    private val curve: ECCurve?
    private val bits: ByteArray?

    // This field is effectively final - once set it won't change again. However it can be set after
    // construction.
    private var point: ECPoint? = null

    constructor(curve: ECCurve?, bits: ByteArray?) {
        this.curve = curve
        this.bits = bits
    }

    constructor(point: ECPoint?) {
        this.point = point
        curve = null
        bits = null
    }

    fun get(): ECPoint {
        return point ?: return curve!!.decodePoint(bits).also {
            point = it
        }
    }

    // Delegated methods.
    val detachedPoint: ECPoint
        get() = get().detachedPoint

    val encoded: ByteArray
        get() = if (bits != null) Arrays.copyOf(bits, bits.size) else get().getEncoded(false)
    val isInfinity: Boolean
        get() = get().isInfinity

    fun timesPow2(e: Int): ECPoint {
        return get()!!.timesPow2(e)
    }

    val yCoord: ECFieldElement
        get() = get()!!.yCoord
    val zCoords: Array<ECFieldElement>
        get() = get()!!.zCoords
    val isNormalized: Boolean
        get() = get()!!.isNormalized

    //    public boolean isCompressed() {
    //        if (bits != null)
    //            return bits[0] == 2 || bits[0] == 3;
    //        else
    //            return get().com;
    //    }
    fun multiply(k: BigInteger?): ECPoint {
        return get()!!.multiply(k)
    }

    fun subtract(b: ECPoint?): ECPoint {
        return get()!!.subtract(b)
    }

    val isValid: Boolean
        get() = get()!!.isValid

    fun scaleY(scale: ECFieldElement?): ECPoint {
        return get()!!.scaleY(scale)
    }

    val xCoord: ECFieldElement
        get() = get()!!.xCoord

    fun scaleX(scale: ECFieldElement?): ECPoint {
        return get()!!.scaleX(scale)
    }

    fun equals(other: ECPoint?): Boolean {
        return get()!!.equals(other)
    }

    fun negate(): ECPoint {
        return get()!!.negate()
    }

    fun threeTimes(): ECPoint {
        return get()!!.threeTimes()
    }

    fun getZCoord(index: Int): ECFieldElement {
        return get()!!.getZCoord(index)
    }

    fun getEncoded(compressed: Boolean): ByteArray {
//        if (compressed == isCompressed() && bits != null)
//            return Arrays.copyOf(bits, bits.length);
//        else
        return get()!!.getEncoded(compressed)
    }

    fun add(b: ECPoint?): ECPoint {
        return get()!!.add(b)
    }

    fun twicePlus(b: ECPoint?): ECPoint {
        return get()!!.twicePlus(b)
    }

    fun getCurve(): ECCurve {
        return get()!!.curve
    }

    fun normalize(): ECPoint {
        return get()!!.normalize()
    }

    val y: ECFieldElement
        get() = this.normalize().yCoord

    fun twice(): ECPoint {
        return get()!!.twice()
    }

    val affineYCoord: ECFieldElement
        get() = get()!!.affineYCoord
    val affineXCoord: ECFieldElement
        get() = get()!!.affineXCoord
    val x: ECFieldElement
        get() = this.normalize().xCoord

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        return if (o == null || javaClass != o.javaClass) false else Arrays.equals(canonicalEncoding, (o as LazyECPoint).canonicalEncoding)
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(canonicalEncoding)
    }

    private val canonicalEncoding: ByteArray
        private get() = getEncoded(true)
}