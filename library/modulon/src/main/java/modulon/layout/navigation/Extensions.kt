package modulon.layout.navigation


import modulon.extensions.view.create

fun BottomNavigationLayout.button(block: BottomNavigationLayout.Item.() -> Unit) = addMenu(create(block))
