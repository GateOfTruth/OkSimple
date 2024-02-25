package com.gateoftruth.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.gateoftruth.oklibrary.OkSimple
import okhttp3.Call
import okhttp3.Response
import java.io.File

class UploadFileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_file)
        val theUrl=""
        if (!theUrl.startsWith("http")){
            Toast.makeText(this,"请先在代码里设置url和文件path", Toast.LENGTH_LONG).show()
            return
        }
        OkSimple.uploadFile(theUrl, File("")).execute(object : GsonCallBack<String>() {
            override fun getData(
                data: String,
                rawBodyString: String,
                call: Call,
                response: Response
            ) {

            }

            override fun failure(call: Call, e: Exception) {

            }

            override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {

            }

            override fun downloadProgress(url: String, total: Long, current: Long) {
            }

        })

    }
}
