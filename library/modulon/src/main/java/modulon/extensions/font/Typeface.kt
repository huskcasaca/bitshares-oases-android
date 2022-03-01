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

// TODO: 2022/2/11 consider replace with tabular inter
val UnionContext.typefaceMonoRegular: Typeface
    get() = Typeface.create(context.resources.getFont(R.font.hack_bold), Typeface.NORMAL)

val UnionContext.typefaceMonoBold: Typeface
    get() = Typeface.create(context.resources.getFont(R.font.hack_bold), Typeface.NORMAL)


val UnionContext.typefaceRegular: Typeface
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (isChinese()) {
//            Typeface.create(context.resources.getFont(R.font.cnfix_inter_family), 600, false)
            Typeface.create(context.resources.getFont(R.font.inter_medium),  Typeface.NORMAL)
        } else {
//            Typeface.create(context.resources.getFont(R.font.inter_family), 600, false)
            Typeface.create(context.resources.getFont(R.font.inter_semi_bold),  Typeface.NORMAL)
        }
//        Typeface.create(context.resources.getFont(R.font.inter_semi_bold), Typeface.NORMAL)
    } else {
        if (isChinese()) {
            Typeface.create(context.resources.getFont(R.font.cnfix_inter_family), Typeface.NORMAL)
        } else {
            Typeface.create(context.resources.getFont(R.font.inter_semi_bold), Typeface.NORMAL)
        }
    }

val UnionContext.typefaceBold: Typeface
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (isChinese()) {
//            Typeface.create(context.resources.getFont(R.font.cnfix_inter_family), 700, false)
            Typeface.create(context.resources.getFont(R.font.inter_semi_bold),  Typeface.BOLD)
        } else {
//            Typeface.create(context.resources.getFont(R.font.inter_family), 700, false)
            Typeface.create(context.resources.getFont(R.font.inter_bold),  Typeface.BOLD)
        }
//        Typeface.create(context.resources.getFont(R.font.inter_bold), Typeface.NORMAL)
    } else {
        if (isChinese()) {
            Typeface.create(context.resources.getFont(R.font.cnfix_inter_family), Typeface.BOLD)
        } else {
            Typeface.create(context.resources.getFont(R.font.inter_bold), Typeface.BOLD)
        }
    }

val UnionContext.typefaceExtraBold: Typeface
    get() = Typeface.create(context.resources.getFont(R.font.inter_extra_bold),  Typeface.BOLD)