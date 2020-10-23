package com.alen.oksimpleutil

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.RequestBuilder

class GlideProgressImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)


    private val baseView: View =
        LayoutInflater.from(context).inflate(R.layout.view_glide_progress, this, true)
    private val imageView = findViewById<ImageView>(R.id.iv_for_glide)
    private val progress = findViewById<ProgressBar>(R.id.progress_glide)

    fun <T> into(url: String, requestBuilder: RequestBuilder<T>) {
        requestBuilder.into(imageView)
        MyGlideCallBack.instance.urlProgressListener[url] = object : ImageProgress {
            override fun downloadProgress(total: Long, current: Long) {
                val totalDouble = total.toDouble()
                var percent = current / totalDouble
                percent *= 100
                progress.progress = percent.toInt()

            }

        }
    }

}