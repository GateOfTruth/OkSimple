package com.alen.oksimpleutil

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alen.oklibrary.FileResultCallBack
import com.alen.oklibrary.OkSimple
import kotlinx.android.synthetic.main.activity_download.*
import okhttp3.Call
import java.io.File

class DownloadActivity : AppCompatActivity() {
    private val url = "https://alissl.ucdl.pp.uc.cn/fs08/2019/09/25/2/110_24d3e649d813bc6029be20a812f56797.apk?fname=%E5%92%AA%E5%92%95%E9%98%85%E8%AF%BB&data=dGVzdFRhZz1uZXdgY3BUeXBlPTIxYGJpZD1udWxsYGRLZXk9OGM2M2Q2ZjlmYzFiNWNjOTllMmRlNDYwNWRkNDQxMWRgaUtleT1lMzhiMDJkODFmMWQzYzJhYzRhZTI5MThkM2VmYzFkZWBkZktleT00MjBhMjA3NzA2ZjFiOWY1NWU4NTY1OTNlYmM2ZTYxMWBhZHhDcFR5cGU9MjE&productid=2011&pkgType=1&packageid=800823369&pkg=com.ophone.reader.ui&vcode=185&yingid=wdj_web&pos=wdj_web%2Fdetail_normal_dl%2F0&appid=280087&apprd=280087&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2019%2F09%2F26%2F10%2F110_bd3a7ab02270602524832113a5512c71_con.png&did=08f7fe32d0b3fa0d40c724875108cc87&md5=fad8c6e11b40cb1fd8cb2822440455e7"
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
            val file=File(path,name)
            if (file.exists())
                file.delete()
        }
        btn_download.setOnClickListener {
            OkSimple.downloadFile(url, name, path).apply {
                tag="123"
            }
                .execute(object :
                    FileResultCallBack(

                    ) {


                    override fun failure(call: Call, e: Exception) {
                        e.printStackTrace()
                    }

                    override fun finish(file: File) {
                        Toast.makeText(
                            this@DownloadActivity,
                            "download success,file length:${file.length()},file path:${file.absolutePath}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun downloadProgressOnMainThread(url:String,total: Long, current: Long) {
                        val d=total.toDouble()
                        val percent=current/d
                        val realPercent=(percent*100).toInt()
                        Log.e("progress","current:$current\t\ttotal:$total\t$realPercent")
                        progress.progress=realPercent
                    }

                })
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        OkSimple.cancelCall("123")
    }
}
