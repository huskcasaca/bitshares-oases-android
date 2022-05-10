package modulon.component.appbar

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import modulon.R
import modulon.extensions.animation.*
import modulon.extensions.view.*
import modulon.extensions.view.doOnClick
import modulon.layout.linear.LinearView
import modulon.widget.FieldTextView

class SearchView(context: Context) : LinearView(context) {

    val fieldtextView: FieldTextView = create {
        background = null
        textColor = context.getColor(R.color.cell_text_primary)
        isSingleLine = true
        textSize = 18f
        layoutWidth = MATCH_PARENT
        doAfterTextChanged {
            containerButton.isVisible = !text.isNullOrEmpty()
        }
    }

    private val containerButton = create<FrameLayout> {
        isVisible = false
        layoutWidth = MATCH_PARENT
        view<ImageView> {
            imageDrawable = R.drawable.ic_cell_back_arrow.contextDrawable() // TODO: 27/1/2022 replace image
            gravity = Gravity.CENTER
            layoutGravityFrame = Gravity.CENTER
        }
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
        addView(fieldtextView)
        addView(containerButton)

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


