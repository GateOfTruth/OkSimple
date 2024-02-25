package com.gateoftruth.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gateoftruth.oklibrary.FileResultCallBack
import com.gateoftruth.oklibrary.OkSimple
import com.gateoftruth.sample.databinding.ActivityDownloadBinding
import okhttp3.Call
import java.io.File

class DownloadActivity : AppCompatActivity() {
    private val url = ""
    private val name = "read.apk"
    private var path = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        path = getExternalFilesDir("download")?.absolutePath ?: ""
        binding.btnPauseDownload.setOnClickListener {
            OkSimple.cancelCall("123")
        }
        binding.btnCancelDownload.setOnClickListener {
            OkSimple.cancelCall("123")
            val file = File(path, name)
            if (file.exists())
                file.delete()
        }
        binding.btnDownload.setOnClickListener {
            if (!url.startsWith("http")){
                Toast.makeText(this,"请先在代码里设置下载地址和文件名",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            OkSimple.downloadFile(url, name, path).apply {
                tag = "123"
            }
                .execute(object :
                    FileResultCallBack(

                    ) {


                    override fun failure(call: Call, e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@DownloadActivity, "下载失败", Toast.LENGTH_SHORT).show()
                    }

                    override fun finish(file: File) {
                        Toast.makeText(
                            this@DownloadActivity,
                            "download success,file length:${file.length()},file path:${file.absolutePath}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun downloadProgressOnMainThread(
                        url: String,
                        total: Long,
                        current: Long
                    ) {
                        val d = total.toDouble()
                        val percent = current / d
                        val realPercent = (percent * 100).toInt()
                        Log.e("progress", "current:$current\t\ttotal:$total\t$realPercent")
                        binding.progressbar.progress = realPercent
                    }

                    override fun start() {

                    }

                })
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        OkSimple.cancelCall("123")
    }
}
