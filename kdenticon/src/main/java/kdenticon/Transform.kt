package kdenticon

class Transform(private val x: Float, private val y: Float, private val size: Float, private val rotation: Float) {

    companion object {
        fun noTransform(): Transform {
            return Transform(0f, 0f, 0f, 0f)
        }
    }

    fun transformPoint(x: Float, y: Float, w: Float? = null, h: Float? = null): Point {
        val right = this.x + this.size
        val bottom = this.y + this.size
        val height = h ?: 0f
        val width = w ?: 0f
        return if (this.rotation == 1f) Point(right - y - height, this.y + x) else
            if (this.rotation == 2f) Point(right - x - width, bottom - y - height) else
                if (this.rotation == 3f) Point(this.x + y, bottom - x - width) else
                    Point(this.x + x, this.y + y)
    }
}
