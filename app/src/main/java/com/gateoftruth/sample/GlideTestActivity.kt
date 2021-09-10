package com.gateoftruth.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_glide_test.*

class GlideTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide_test)
        val url = "picture url"
        val url2 = "picture url"
        val options = RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
        glideProgressImageView.into(url, GlideApp.with(this).load(url).apply(options))
        glideProgressImageView2.into(url2, GlideApp.with(this).load(url2).apply(options))

    }
}
