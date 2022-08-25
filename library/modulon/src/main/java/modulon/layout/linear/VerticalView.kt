package modulon.layout.linear

import android.content.Context
import modulon.extensions.viewbinder.noClipping
import modulon.extensions.viewbinder.noMotion

open class VerticalView(context: Context) : LinearView(context) {

    init {
        orientation = VERTICAL
//        layoutAnimation = Animation.DEFAULT
//        layoutTransition = TransitionExtended.EXTENDED
        // TODO: 20/1/2022 apply to all layout
        noClipping()
        noMotion()
    }

}

