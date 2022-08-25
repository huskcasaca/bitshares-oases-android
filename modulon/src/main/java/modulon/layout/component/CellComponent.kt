package modulon.layout.component

import modulon.widget.PlainTextView

interface TitleComponent {
    var title: CharSequence
    val titleView: PlainTextView
}
interface SubtitleComponent {
    var subtitle: CharSequence
    val subtitleView: PlainTextView
}
interface TextComponent {
    // TODO: 2022/2/20 add extension for textColor
    var text: CharSequence
    val textView: PlainTextView
}

interface SubtextComponent {
    var subtext: CharSequence
    val subtextView: PlainTextView
}

interface HeaderComponent: TitleComponent, SubtitleComponent

interface BodyComponent: TextComponent, SubtextComponent

interface CellComponent: HeaderComponent, BodyComponent

