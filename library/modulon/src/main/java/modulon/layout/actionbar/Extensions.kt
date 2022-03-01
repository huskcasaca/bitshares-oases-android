package modulon.layout.actionbar

import modulon.extensions.text.toStringOrEmpty

fun ActionBarLayout.doOnExpand(listener: () -> Unit) {
    addOnExpandListener(listener)
}

fun ActionBarLayout.doOnCollapse(listener: () -> Unit) {
    addOnCollapseListener(listener)
}

fun ActionBarLayout.menu(block: ActionBarLayout.Item.() -> Unit) {
    addMenu(ActionBarLayout.Item(context).apply(block))
}

fun ActionBarLayout.actionMenu(block: ActionBarLayout.Item.() -> Unit) {
    actionButton.apply(block)
}


fun ActionBarLayout.title(text: CharSequence) {
    title = text.toStringOrEmpty()
}

fun ActionBarLayout.subtitle(text: CharSequence, allCaps: Boolean = true) {
    subtitle = if (allCaps) text.toStringOrEmpty().toUpperCase() else text.toStringOrEmpty()
}