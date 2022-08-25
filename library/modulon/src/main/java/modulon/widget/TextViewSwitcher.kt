package modulon.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewSwitcher
import modulon.extensions.compat.isForceDarkAllowedCompat

import modulon.union.toUnion
import modulon.union.UnionContext

class TextViewSwitcher(context: Context) : ViewSwitcherImpl<TextView>(context), UnionContext by context.toUnion() {

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        require(child is TextView) { "TextSwitcher children must be instances of TextView" }
        super.addView(child, index, params)
    }

    var text: CharSequence
        get() = currentView.text
        set(value) {
            nextView.text = value
            showNext()
        }

    var currentText: CharSequence
        get() = currentView.text
        set(value) {
            currentView.text = value
        }
}

open class ViewSwitcherImpl<V : View>(context: Context) : ViewSwitcher(context) {

    override fun getCurrentView(): V {
        return super.getCurrentView() as V
    }

    override fun getNextView(): V {
        return super.getNextView() as V
    }

    init {
        isForceDarkAllowedCompat = false
    }

}