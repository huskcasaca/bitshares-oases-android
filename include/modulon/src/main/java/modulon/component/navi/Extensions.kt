package modulon.component.navi


import modulon.extensions.view.create

fun NaviView.button(block: NaviView.Item.() -> Unit) = addMenu(create(block))
