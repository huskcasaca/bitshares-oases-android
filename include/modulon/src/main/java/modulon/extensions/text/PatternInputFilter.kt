package modulon.extensions.text

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class PatternInputFilter(private val regex: Regex) : InputFilter {

    constructor(pattern: Pattern) : this(pattern.toRegex())

    override fun filter(charSeq: CharSequence?, start: Int, end: Int, span: Spanned?, dStart: Int, dEnd: Int): CharSequence? {
        charSeq?.let { source ->
            for (inputChar in source) {
                if (!inputChar.toString().matches(regex)) {
                    return if (source.length == 1) {
                        ""
                    } else {
                        span?.toString()
                    }
                }
            }
        }
        return charSeq
    }
}