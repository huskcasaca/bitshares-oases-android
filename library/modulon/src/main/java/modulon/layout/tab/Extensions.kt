package modulon.layout.tab

fun TabLayout.tab(block: TabLayout.TabView.() -> Unit) {
    addTab(TabLayout.TabView(context).apply(block))
}
