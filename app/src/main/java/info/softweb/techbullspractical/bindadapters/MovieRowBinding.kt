package info.softweb.techbullspractical.bindadapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import info.softweb.techbullspractical.R

class MovieRowBinding {
    companion object{
        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
        }
    }

}