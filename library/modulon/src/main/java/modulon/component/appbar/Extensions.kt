package modulon.component.appbar

import modulon.extensions.text.toStringOrEmpty

fun AppbarView.doOnExpand(listener: () -> Unit) {
    addOnExpandListener(listener)
}

fun AppbarView.doOnCollapse(listener: () -> Unit) {
    addOnCollapseListener(listener)
}

fun AppbarView.menu(block: AppbarView.Item.() -> Unit) {
    addMenu(AppbarView.Item(context).apply(block))
}

fun AppbarView.actionMenu(block: AppbarView.Item.() -> Unit) {
    actionButton.apply(block)
}


fun AppbarView.title(text: CharSequence) {
    title = text.toStringOrEmpty()
}

fun AppbarView.subtitle(text: CharSequence, allCaps: Boolean = true) {
    subtitle = if (allCaps) text.toStringOrEmpty().toUpperCase() else text.toStringOrEmpty()
}