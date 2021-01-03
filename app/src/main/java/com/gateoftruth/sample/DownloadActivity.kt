package com.gateoftruth.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gateoftruth.oklibrary.FileResultCallBack
import com.gateoftruth.oklibrary.OkSimple
import kotlinx.android.synthetic.main.activity_download.*
import okhttp3.Call
import java.io.File

class DownloadActivity : AppCompatActivity() {
    private val url =
        "https://sqdd.myapp.com/myapp/qqteam/qq_hd/apad/qqhd_hd_5.8.8.3445_release.apk"
    private val name = "read.apk"
    private var path = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        path = getExternalFilesDir("download")?.absolutePath ?: ""
        btn_pause_download.setOnClickListener {
            OkSimple.cancelCall("123")
        }
        btn_cancel_download.setOnClickListener {
            OkSimple.cancelCall("123")
            val file = File(path, name)
            if (file.exists())
                file.delete()
        }
        btn_download.setOnClickListener {
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
                        progress.progress = realPercent
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
