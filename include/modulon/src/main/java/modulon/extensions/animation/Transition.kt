package modulon.extensions.animation

import android.animation.LayoutTransition

val extendedLayoutTransition
    get() = object : LayoutTransition() {
        init {
            setAnimateParentHierarchy(false)
            enableTransitionType(CHANGING)
        }
    }

val defaultLayoutTransition = object : LayoutTransition() {
    init {
        setAnimateParentHierarchy(true)
//        disableTransitionType(CHANGE_APPEARING)
//        disableTransitionType(CHANGE_DISAPPEARING)
    }
}

val visibilityLayoutTransition = object : LayoutTransition() {
    init {
        setAnimateParentHierarchy(false)
        disableTransitionType(CHANGING)
//        disableTransitionType(CHANGE_DISAPPEARING)
    }
}


object TransitionExtended {

    val EXTENDED get() = extendedLayoutTransition

    val DEFAULT = defaultLayoutTransition

}
