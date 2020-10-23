package com.alen.oksimpleutil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alen.oklibrary.OkSimple
import okhttp3.Call
import okhttp3.Response
import java.io.File

class UploadFileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_file)
        OkSimple.uploadFile("",File("")).execute(object :GsonCallBack<String>(){
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
