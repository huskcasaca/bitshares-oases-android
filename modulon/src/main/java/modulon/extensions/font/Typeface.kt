package modulon.extensions.font

import android.graphics.Typeface
import android.os.Build
import modulon.R
import modulon.UI.ENABLE_CHINESE_FONT_FIXES
import modulon.union.UnionContext
import modulon.widget.FieldTextView
import modulon.widget.PlainTextView
import java.util.*

fun UnionContext.isChinese() = ENABLE_CHINESE_FONT_FIXES && context.resources.configuration.locales.get(0).let { it == Locale.SIMPLIFIED_CHINESE || it == Locale.TRADITIONAL_CHINESE }


fun PlainTextView.typefaceRegular() {
    typeface = typefaceRegular
}

fun PlainTextView.typefaceBold() {
    typeface = typefaceBold
}

fun FieldTextView.typefaceRegular() {
    typeface = typefaceRegular
}

fun FieldTextView.typefaceBold() {
    typeface = typefaceBold
}


val UnionContext.typefaceDinMedium: Typeface
    get() = Typeface.create(context.resources.getFont(R.font.dinb), Typeface.NORMAL)

private var hack_bold: Typeface? = null

// TODO: 2022/2/11 consider replace with tabular inter
val UnionContext.typefaceMonoRegular: Typeface
    get() = hack_bold ?: Typeface.create(context.resources.getFont(R.font.hack_bold), Typeface.NORMAL).also { hack_bold = it }

val UnionContext.typefaceMonoBold: Typeface
    get() = hack_bold ?: Typeface.create(context.resources.getFont(R.font.hack_bold), Typeface.NORMAL).also { hack_bold = it }


private var inter_medium: Typeface? = null
private var inter_semi_bold: Typeface? = null
private var cnfix_inter_family: Typeface? = null


val UnionContext.typefaceRegular: Typeface
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (isChinese()) {
//            Typeface.create(context.resources.getFont(R.font.cnfix_inter_family), 600, false)
            inter_medium ?: Typeface.create(context.resources.getFont(R.font.inter_medium),  Typeface.NORMAL).also {
                inter_medium = it
            }

        } else {
//            Typeface.create(context.resources.getFont(R.font.inter_family), 600, false)
            inter_semi_bold ?: Typeface.create(context.resources.getFont(R.font.inter_semi_bold),  Typeface.NORMAL).also {
                inter_semi_bold = it
            }
        }
//        Typeface.create(context.resources.getFont(R.font.inter_semi_bold), Typeface.NORMAL)
    } else {
        if (isChinese()) {
            cnfix_inter_family ?: Typeface.create(context.resources.getFont(R.font.cnfix_inter_family), Typeface.NORMAL).also {
                cnfix_inter_family = it
            }
        } else {
            inter_semi_bold ?: Typeface.create(context.resources.getFont(R.font.inter_semi_bold), Typeface.NORMAL).also {
                inter_semi_bold = it
            }
        }
    }


private var inter_semi_bold_bold: Typeface? = null
private var inter_bold: Typeface? = null

private var cnfix_inter_family_bold: Typeface? = null

val UnionContext.typefaceBold: Typeface
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (isChinese()) {
//            Typeface.create(context.resources.getFont(R.font.cnfix_inter_family), 700, false)
            inter_semi_bold_bold ?: Typeface.create(context.resources.getFont(R.font.inter_semi_bold), Typeface.BOLD).also {
                inter_semi_bold_bold = it
            }
        } else {
//            Typeface.create(context.resources.getFont(R.font.inter_family), 700, false)
            inter_bold ?: Typeface.create(context.resources.getFont(R.font.inter_bold), Typeface.BOLD).also {
                inter_bold = it
            }
        }
//        Typeface.create(context.resources.getFont(R.font.inter_bold), Typeface.NORMAL)
    } else {
        if (isChinese()) {
            cnfix_inter_family_bold ?: Typeface.create(context.resources.getFont(R.font.cnfix_inter_family), Typeface.BOLD).also {
                cnfix_inter_family_bold = it
            }
        } else {
            inter_bold ?: Typeface.create(context.resources.getFont(R.font.inter_bold), Typeface.BOLD).also {
                inter_bold = it
            }
        }
    }

val UnionContext.typefaceExtraBold: Typeface
    get() = Typeface.create(context.resources.getFont(R.font.inter_extra_bold),  Typeface.BOLD)