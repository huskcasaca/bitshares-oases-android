package kdenticon

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Color {
    companion object {
        fun decToHex(v: Int): String {
            val capped = min(max(v, 0), 255)
            return capped.toString(16)
        }

        fun hueToRgb(m1: Float, m2: Float, h: Float): String {
            val h2 = if (h < 0) h + 6 else if (h > 6) h - 6 else h
            return decToHex((floor(255 * (
                    if (h2 < 1) m1 + (m2 - m1) * h2 else
                        if (h2 < 3) m2 else
                            if (h2 < 4) m1 + (m2 - m1) * (4 - h2) else
                                m1
                    ))).toInt())
        }

        fun rgb(r: Int, g: Int, b: Int): String {
            return StringBuilder()
                    .append("#")
                    .append(decToHex(r))
                    .append(decToHex(g))
                    .append(decToHex(b))
                    .toString()
        }

        fun parse(color: String) : String {
            val re = Regex("^#[0-9a-f]{3,8}$", RegexOption.IGNORE_CASE)
            if (re.matches(color)) {
                if (color.length < 6) {
                    val r = color[1]
                    val g = color[2]
                    val b = color[3]
                    val a = if (color.length > 4) color[4] else ""
                    return "#$r$r$g$g$b$b$a$a"
                }
                if (color.length == 7 || color.length > 8) {
                    return color
                }
            }
            return "#000000"
        }

        /**
         * @param hexColor  Color on the format "#RRGGBB" or "#RRGGBBAA"
         */
        fun toCss3(hexColor: String) : String {
            var a = 1
            try {
                a = hexColor.substring(7, 2).toInt(16)
            } catch (e: NumberFormatException) {
                return hexColor
            }
            val r = hexColor.substring(1, 2).toInt(16)
            val g = hexColor.substring(3, 2).toInt(16)
            val b = hexColor.substring(5, 2).toInt(16)
            return "rgba(" + r + "," + g + "," + b + "," + String.format("%02f", a / 255f) + ")"
        }
        /**
         * @param h Hue [0, 1]
         * @param s Saturation [0, 1]
         * @param l Lightness [0, 1]
         */
        fun hsl(h: Float, s: Float, l: Float) : String {
            // Based on http://www.w3.org/TR/2011/REC-css3-color-20110607/#hsl-color
            return if (s == 0f) {
                val partialHex = decToHex(floor(l * 255f).toInt())
                "#$partialHex$partialHex$partialHex"
            } else {
                val m2 = if (l <= 0.5f)  l * (s + 1) else l + s - l * s
                val m1 = l * 2 - m2
                "#" +
                        hueToRgb(m1, m2, (h * 6f + 2f)) +
                        hueToRgb(m1, m2, (h * 6f)) +
                        hueToRgb(m1, m2, (h * 6f - 2f))
            }
        }
        // This function will correct the lightness for the "dark" hues
        fun correctedHsl(h: Float, s: Float, l: Float) : String {
            // The corrector specifies the perceived middle lightnesses for each hue
            val correctors = arrayOf( 0.55f, 0.5f, 0.5f, 0.46f, 0.6f, 0.55f, 0.55f )
            val corrector = correctors[floor(h * 6 + 0.5).toInt()]

            // Adjust the input lightness relative to the corrector
            val l2 = if (l < 0.5f) l * corrector * 2f else corrector + (l - 0.5f) * (1f - corrector) * 2f

            return hsl(h, s, l2)
        }
    }
}