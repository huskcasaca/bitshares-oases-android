package modulon.extensions.view

import android.text.method.ReplacementTransformationMethod
import android.view.inputmethod.EditorInfo

object InputTypeExtended {

    // TODO: 20/9/2021 replace EditorInfo with InputTypeExtended
    const val TYPE_PASSWORD = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
    const val TYPE_PASSWORD_VISIBLE = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    const val TYPE_NO_SUGGESTION = EditorInfo.TYPE_TEXT_VARIATION_FILTER or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    const val TYPE_NUMBER_DECIMAL = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
    const val TYPE_NUMBER_SIGNED = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_SIGNED

    const val TYPE_URI_NO_SUGGESTION = EditorInfo.TYPE_TEXT_VARIATION_URI or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS

}

