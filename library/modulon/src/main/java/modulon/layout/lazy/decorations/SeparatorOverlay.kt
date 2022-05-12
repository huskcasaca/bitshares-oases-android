package modulon.layout.lazy.decorations

//class SeparatorOverlay(context: Context) : RecyclerView.ItemDecoration(), UnionContext by context.toUnion() {
//
//    private val bounds = Rect()
//    private val dividerBounds = RectF()
//    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = context.getColor(R.color.component_separator)
//    }
//
//    private val height = 1.5.dpf
//
//    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        super.onDrawOver(canvas, parent, state)
//        if (parent.itemAnimator?.isRunning == true) return
//        var lastView: View? = null
//        parent.forEach {
//            val temp = lastView
//            if (temp != null && shouldDrawOver(temp) && shouldDrawOver(it)) {
//                parent.getDecoratedBoundsWithMargins(it, bounds)
//                moveBoundsWithTranslation(it, bounds)
//                if (bounds.top != bounds.bottom) {
//                    dividerBounds.apply {
//                        set(bounds)
//                        top = bounds.top + height / 2
//                        bottom = bounds.top - height / 2
//                    }
//                    canvas.drawRect(dividerBounds, paint)
//                }
//            }
//            lastView = it
//        }
//    }
//
//    private val transparentBackground = R.color.transparent.contextColor()
//
//
//    private val Drawable.tintCompat: Int
//        get() = when (this) {
//            is RippleDrawable -> getDrawable(0).tintCompat
//            is ColorDrawable -> color
//            is ShapeDrawable -> paint.color
//            is GradientDrawable -> colors?.let { ColorUtils.blendARGB(it.first(), it.last(), 0.5f) } ?: 0
//            else -> 0
//        }
//
//
//    private fun shouldDrawOver(view: View) : Boolean {
//        val inner = if (view is GroupedRowHolder.Item) view.child ?: return false else view
//        return inner !is RecyclerHeaderSpacer && inner !is RecyclerContentLocator  && inner !is RecyclerHeader && inner.background?.tintCompat.let { it != null && it != transparentBackground }
//    }
//
//
//}