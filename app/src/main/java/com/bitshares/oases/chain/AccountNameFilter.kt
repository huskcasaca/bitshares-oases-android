package com.bitshares.oases.chain

import android.text.InputFilter
import android.text.Spanned
import java.util.*

object AccountNameFilter : InputFilter {

    private const val digits = "0123456789abcdefghijklmnopqrstuvwxyz-."

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        val lowercase = source.toString().toLowerCase(Locale.ROOT)
        val filtered = lowercase.filter { digits.contains(it) }
        if (dstart == 0 && filtered.isNotEmpty() && (filtered.startsWith('-'))) return filtered.subSequence(1, end)
        return filtered
    }

}