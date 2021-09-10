package com.gateoftruth.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import com.gateoftruth.oklibrary.OkSimple
import okhttp3.Call
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream

class PostFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_form)
        val fileName = "stars1.jpg"
        val inputStream = assets.open(fileName)
        val file = File(getExternalFilesDir("pic"), fileName)
        if (file.exists()) {
            file.delete()
        }
        val fileOutputStream = FileOutputStream(file)
        inputStream.use { input ->
            fileOutputStream.use { out ->
                input.copyTo(out)
            }
        }

        OkSimple.postForm("post url").addFormPart("image", file, "image/*")
            .addFormPart("route", "uploadFile").execute(object :
                GsonCallBack<Any>() {
                override fun getData(
                    data: Any,
                    rawBodyString: String,
                    call: Call,
                    response: Response
                ) {
                    Log.e("getdata", rawBodyString)
                }

                override fun failure(call: Call, e: Exception) {
                    e.printStackTrace()
                }

                override fun uploadProgressOnMainThread(
                    fileName: String,
                    total: Long,
                    current: Long
                ) {
                    Log.e("postForm", "$fileName\t\t$total\t\t$current")
                }

            })
    }
}
