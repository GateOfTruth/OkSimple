package com.alen.oksimpleutil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_glide_test.*

class GlideTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide_test)
        val url="https://images.pexels.com/photos/1837591/pexels-photo-1837591.jpeg?cs=srgb&dl=architecture-big-ben-bird-s-eye-view-1837591.jpg&fm=jpg"
        val url2="https://images.pexels.com/photos/1414050/pexels-photo-1414050.jpeg?cs=srgb&dl=architecture-big-wheel-buoy-1414050.jpg&fm=jpg"
        val options=RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
        glideProgressImageView.into(url,GlideApp.with(this).load(url).apply(options))
        glideProgressImageView2.into(url2,GlideApp.with(this).load(url2).apply(options))

    }
}
