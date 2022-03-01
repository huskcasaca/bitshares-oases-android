package kdenticon

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import com.caverock.androidsvg.SVG

class KdenticonView(context: Context) : ImageView(context) {

    companion object {
        private const val DEFAULT_SEED = ""
        private const val DEFAULT_HASH_ALGORITHM = HashUtils.MD5
        private const val DEFAULT_SIZE = 100
        private const val DEFAULT_PADDING = 0.1f

        private const val KEY_SEED = "key_seed"
        private const val KEY_HASH_ALGORITHM = "key_hash_algorithm"
        private const val KEY_SIZE = "key_size"
        private const val KEY_PADDING = "key_padding"
        private const val KEY_SUPER_STATE = "key_super_state"
    }

    var seed = DEFAULT_SEED
        set(value) {
            field = value
            setDrawable()
        }

    var hash = DEFAULT_HASH_ALGORITHM
        set(value) {
            field = value
//            setDrawable()
        }

    var size: Int = DEFAULT_SIZE
        set(value) {
            field = value
            setDrawable()
        }

    var padding: Float = DEFAULT_PADDING
        set(value) {
            field = value
            setDrawable()
        }

//    val kdenticonDrawable: Drawable
//        get() {
//            return renderDrawable()
//        }

//    var colorList = listOf(0)

    init {
        setBackgroundResource(android.R.color.transparent)
    }

    private fun setDrawable() {
        setImageDrawable(renderDrawable())
    }

    private fun renderDrawable(): Drawable{
        val hashString = when(hash) {
            HashUtils.MD5 -> HashUtils.md5(seed)
            HashUtils.SHA1 -> HashUtils.sha1(seed)
            HashUtils.SHA256 -> HashUtils.sha256(seed)
            HashUtils.SHA512 -> HashUtils.sha512(seed)
            else -> ""
        }
        val svgString = Kdenticon.toSvg(hashString, size, padding)
//        colorList = Regex("fill=\"\\#([a-f,0-9]{6}?)\"").findAll(svgString).map { parseInt(it.groupValues[1], 16) }.toList()
        return PictureDrawable(SVG.getFromString(svgString).renderToPicture())
    }


    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putString(KEY_SEED, seed)
        bundle.putInt(KEY_HASH_ALGORITHM, hash)
        bundle.putInt(KEY_SIZE, size)
        bundle.putFloat(KEY_PADDING, padding)
        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var viewState = state
        if (viewState is Bundle) {
            seed = viewState.getString(
                KEY_SEED,
                DEFAULT_SEED
            )
            hash = viewState.getInt(
                KEY_HASH_ALGORITHM,
                DEFAULT_HASH_ALGORITHM
            )
            size = viewState.getInt(
                KEY_SIZE,
                DEFAULT_SIZE
            )
            padding = viewState.getFloat(
                KEY_PADDING,
                DEFAULT_PADDING
            )
            viewState = viewState.getParcelable(KEY_SUPER_STATE)
        }

        super.onRestoreInstanceState(viewState)
    }
}
