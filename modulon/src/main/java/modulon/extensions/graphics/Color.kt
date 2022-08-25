package modulon.extensions.graphics

import android.graphics.drawable.*
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

fun blendColorARGB(color1: Int, color2: Int, ratio: Float) = ColorUtils.blendARGB(color1, color2, ratio)

fun Int.alphaColor(alpha: Float) = ColorUtils.setAlphaComponent(this, (alpha.coerceIn(0f..1f) * 255).roundToInt())
