package modulon.extensions.view

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import modulon.layout.recycler.RecyclerLayout

var TextView.textSolidColor: Int
    @JvmName("getTextColorKt") get() = currentTextColor
    @JvmName("setTextColorKt") set(value) {
        setTextColor(value)
    }

var ImageView.filterColor: Int
    get() = 0
    set(value) {
        setColorFilter(value)
    }

var View.isScrollBarEnabled
    get() = isHorizontalScrollBarEnabled && isVerticalScrollBarEnabled
    set(value) {
        isHorizontalScrollBarEnabled = value
        isVerticalScrollBarEnabled = value
    }


var ScrollView.edgeEffectColor: Int
    get() = 0
    set(value) {
        try {
            javaClass.getDeclaredField("mEdgeGlowTop").apply {
                isAccessible = true
                (get(this@edgeEffectColor) as EdgeEffect).color = value
            }
            javaClass.getDeclaredField("mEdgeGlowBottom").apply {
                isAccessible = true
                (get(this@edgeEffectColor) as EdgeEffect).color = value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


var HorizontalScrollView.edgeEffectColor: Int
    get() = 0
    set(value) {
//        leftEdgeEffectColor = value
//        rightEdgeEffectColor = value
//

//        this::class.members.forEach {
//            logcat("HorizontalScrollView  declaredMemberProperties  ${it.name}")
//        }
//        javaClass.fields.forEach {
//            logcat("HorizontalScrollView  fields  ${it.name}")
//        }
//        javaClass.declaredFields.forEach {
//            logcat("HorizontalScrollView  declaredFields  ${it.name}")
//        }
//        try {
//            javaClass.getDeclaredField("mEdgeGlowTop").apply {
//                isAccessible = true
//                (get(this@edgeEffectColor) as EdgeEffect).color = value
//            }
//            javaClass.getDeclaredField("mEdgeGlowRight").apply {
//                isAccessible = true
//                (get(this@edgeEffectColor) as EdgeEffect).color = value
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

// TODO: 14/11/2021 proguard rules required
var ViewPager2.edgeEffectColor: Int
    // TODO: 15/11/2021 get edgeEffectColor
    get() = 0
    set(value) {
        try {
            javaClass.getDeclaredField("mRecyclerView").apply {
                isAccessible = true
                (get(this@edgeEffectColor) as RecyclerView).edgeEffectColor = value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

var ViewPager2.edgeEffectFactory: RecyclerView.EdgeEffectFactory
    // TODO: 15/11/2021 get edgeEffectColor
    get() {
        return javaClass.getDeclaredField("mRecyclerView").run {
            isAccessible = true
            (get(this@edgeEffectFactory) as RecyclerView).edgeEffectFactory
        }
    }
    set(value) {
        try {
            javaClass.getDeclaredField("mRecyclerView").apply {
                isAccessible = true
                (get(this@edgeEffectFactory) as RecyclerView).edgeEffectFactory = value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


var RecyclerView.edgeEffectColor: Int
    get() = 0
    set(value) {
        edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                return EdgeEffect(view.context).apply { this.color = value }
            }
        }
    }


val RecyclerLayout.isOnTop
    get() = (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() == 0

val RecyclerLayout.isOnBottom
    get() = (layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition() == (layoutManager as? LinearLayoutManager)!!.itemCount - 1