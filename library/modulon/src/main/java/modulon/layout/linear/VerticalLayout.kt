package modulon.layout.linear

import android.content.Context
import modulon.extensions.animation.TransitionExtended
import modulon.extensions.viewbinder.noClipping

open class VerticalLayout(context: Context) : LinearLayout(context) {

    init {
        orientation = VERTICAL
//        layoutAnimation = Animation.DEFAULT
        layoutTransition = TransitionExtended.EXTENDED
        // TODO: 20/1/2022 apply to all layout
        noClipping()
    }

}

