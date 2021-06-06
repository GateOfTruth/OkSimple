package com.gateoftruth.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_download.setOnClickListener {
            startActivity(Intent(this, DownloadActivity::class.java))
        }
        btn_get.setOnClickListener {
            startActivity(Intent(this, GetActivity::class.java))
        }

        btn_post_params.setOnClickListener {
            startActivity(Intent(this, PostParamsActivity::class.java))
        }

        btn_post_json.setOnClickListener {
            startActivity(Intent(this, PostJsonActivity::class.java))
        }

        btn_bitmap.setOnClickListener {
            startActivity(Intent(this, BitmapActivity::class.java))
        }

        btn_upload.setOnClickListener {
            startActivity(Intent(this, UploadFileActivity::class.java))
        }

        btn_post_form.setOnClickListener {
            startActivity(Intent(this, PostFormActivity::class.java))
        }

        btn_glide_test.setOnClickListener {
            startActivity(Intent(this, GlideTestActivity::class.java))
        }

        btn_brotli_test.setOnClickListener {
            startActivity(Intent(this, BrotliActivity::class.java))
        }

        btn_multiple_download.setOnClickListener {
            startActivity(Intent(this, MultipleDownloadActivity::class.java))
        }

        btn_synchronize_request.setOnClickListener {
            startActivity(Intent(this, SynchronizeRequestActivity::class.java))
        }

    }
}
