package com.bitshares.oases.chain

import android.text.InputFilter
import android.text.Spanned
import java.util.*

object AssetSymbolFilter : InputFilter {

    private const val digits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ."

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        val uppercase = source.toString().toUpperCase(Locale.ROOT)
        val filtered = uppercase.filter { digits.contains(it) }
        if (dstart == 0 && filtered.isNotEmpty() && filtered[0].toString() == ".") return filtered.subSequence(1, end)
        return filtered
    }


}

