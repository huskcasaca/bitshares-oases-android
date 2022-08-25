package modulon.extensions.text

import android.text.method.ReplacementTransformationMethod


val TABULAR_TRANSFORMATION_METHOD get() = object : ReplacementTransformationMethod() {
    override fun getOriginal(): CharArray = CharArray(10) { '\u0030'.plus(it) }
    override fun getReplacement(): CharArray = CharArray(10) { '\uE071'.plus(it) }
}

