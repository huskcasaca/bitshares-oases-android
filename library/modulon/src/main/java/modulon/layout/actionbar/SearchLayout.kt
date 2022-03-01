package modulon.layout.actionbar

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import modulon.R
import modulon.extensions.animation.*
import modulon.extensions.view.*
import modulon.extensions.view.addFill
import modulon.extensions.view.addWrap
import modulon.extensions.view.doOnClick
import modulon.layout.linear.LinearLayout
import modulon.widget.FieldTextView

class SearchLayout(context: Context) : LinearLayout(context) {

    val fieldtextView: FieldTextView = create {
        background = null
        textColor = context.getColor(R.color.cell_text_primary)
        isSingleLine = true
        textSize = 18f
        doAfterTextChanged {
            containerButton.isVisible = !text.isNullOrEmpty()
        }
    }

    private val clearButton: ImageView = create {
        // TODO: 27/1/2022 replace image
        imageDrawable = R.drawable.ic_cell_back_arrow.contextDrawable()
        gravity = Gravity.CENTER
    }

    private val containerButton = create<FrameLayout> {
        addWrap(clearButton, gravity = Gravity.CENTER)
        isVisible = false
        doOnClick {
            fieldtextView.text?.clear()
        }
    }

    var queryHint: CharSequence
        get() = fieldtextView.hint
        set(value) {
            fieldtextView.hint = value
        }

    init {
        setPadding(0.dp, 0, 0.dp, 0)
        orientation = HORIZONTAL
        layoutTransition = TransitionExtended.EXTENDED
        addFill(fieldtextView)
        addFill(containerButton)
        fieldtextView.requestFocus()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY)
        )
        if (containerButton.isVisible) containerButton.measure(
            MeasureSpec.makeMeasureSpec(48.dp, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(48.dp, MeasureSpec.EXACTLY)
        )
        fieldtextView.measure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) - if (containerButton.isVisible) containerButton.measuredWidth else 0, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY)
        )
    }


}


