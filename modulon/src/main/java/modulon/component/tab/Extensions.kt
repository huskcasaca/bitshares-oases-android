package modulon.component.tab

fun TabView.tab(block: TabView.TabView.() -> Unit) {
    addTab(TabView.TabView(context).apply(block))
}
