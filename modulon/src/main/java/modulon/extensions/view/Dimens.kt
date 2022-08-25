package modulon.extensions.view

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import kotlin.math.abs
import kotlin.math.ceil


// TODO: 1/2/2022
private val displayMetrics = Resources.getSystem().displayMetrics

/** Convert DP to [Int] PX */
val Number.dp: Int
    get() = if (this == 0) 0 else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), displayMetrics).toInt()

/** Convert DP to [Float] PX */
val Number.dpf: Float
    get() = if (this == 0) 0f else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), displayMetrics)

/** Convert SP to [Int] PX */
val Number.sp: Int
    get() = if (this == 0) 0 else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), displayMetrics).toInt()

/** Convert SP to [Float] PX */
val Number.spf: Float
    get() = if (this == 0) 0f else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), displayMetrics)

/** Display size in [Point] unit */
val displaySize: Point = Point()
    get() {
        val metrics = Resources.getSystem().displayMetrics
        val configuration = Resources.getSystem().configuration
        if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
            val newSize = ceil(configuration.screenWidthDp * metrics.density)
            if (abs(field.x - newSize) > 3) {
                field.x = newSize.toInt()
            }
        }
        if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
            val newSize = ceil(configuration.screenHeightDp * metrics.density)
            if (abs(field.y - newSize) > 3) {
                field.y = newSize.toInt()
            }
        }
        return field
    }

@Suppress("DEPRECATION")
val Context.isSmallScreenCompat: Boolean
    get() {
//    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayMetrics = Resources.getSystem().displayMetrics
        return displayMetrics.widthPixels <= 400.dp
    }

