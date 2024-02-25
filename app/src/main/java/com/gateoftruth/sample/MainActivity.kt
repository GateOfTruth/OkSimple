package com.gateoftruth.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gateoftruth.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnDownload.setOnClickListener {
            startActivity(Intent(this, DownloadActivity::class.java))
        }
        binding.btnGet.setOnClickListener {
            startActivity(Intent(this, GetActivity::class.java))
        }

        binding.btnPostParams.setOnClickListener {
            startActivity(Intent(this, PostParamsActivity::class.java))
        }

        binding.btnPostJson.setOnClickListener {
            startActivity(Intent(this, PostJsonActivity::class.java))
        }

        binding.btnBitmap.setOnClickListener {
            startActivity(Intent(this, BitmapActivity::class.java))
        }

        binding.btnUpload.setOnClickListener {
            startActivity(Intent(this, UploadFileActivity::class.java))
        }

        binding.btnPostForm.setOnClickListener {
            startActivity(Intent(this, PostFormActivity::class.java))
        }

        binding.btnGlideTest.setOnClickListener {
            startActivity(Intent(this, GlideTestActivity::class.java))
        }

        binding.btnBrotliTest.setOnClickListener {
            startActivity(Intent(this, BrotliActivity::class.java))
        }

        binding.btnMultipleDownload.setOnClickListener {
            Toast.makeText(this,"多文件同时下载请视具体逻辑更改，代码仅供参考", Toast.LENGTH_LONG).show()
            return@setOnClickListener
            //startActivity(Intent(this, MultipleDownloadActivity::class.java))
        }

        binding.btnSynchronizeRequest.setOnClickListener {
            startActivity(Intent(this, SynchronizeRequestActivity::class.java))
        }

        binding.btnMethodRequest.setOnClickListener {
            startActivity(Intent(this, MethodActivity::class.java))
        }

    }
}
