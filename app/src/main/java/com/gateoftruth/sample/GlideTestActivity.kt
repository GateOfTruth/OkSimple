package com.gateoftruth.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gateoftruth.sample.databinding.ActivityGlideTestBinding

class GlideTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityGlideTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val url = " url"
        val url2 = "picture url"
        val options = RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
        if (!url.startsWith("http")||!url2.startsWith("http")){
            Toast.makeText(this,"要查看图片加载的进度条，请先在代码里设置图片url", Toast.LENGTH_LONG).show()
            return
        }
        binding.glideProgressImageView.into(url, Glide.with(this).load(url).apply(options))
        binding.glideProgressImageView2.into(url2, Glide.with(this).load(url2).apply(options))

    }
}
