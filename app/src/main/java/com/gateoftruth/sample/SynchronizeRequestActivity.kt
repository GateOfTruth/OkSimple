package com.gateoftruth.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gateoftruth.oklibrary.OkSimple

class SynchronizeRequestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synchronize_request)
        val theUrl=""
        val thread=object :Thread(){
            override fun run() {
                val bean=OkSimple.get(theUrl,isSync = true)
                    .params("month", "1")
                    .params("day", "1")
                    .params("key", "").execute(GsonSyncBeanBase(ExampleBean::class.java)).responseToData()
                val result=bean?.result
            }
        }

        if (!theUrl.startsWith("http")){
            Toast.makeText(this@SynchronizeRequestActivity,"请先在代码里设置url", Toast.LENGTH_LONG).show()
            return
        }
        thread.start()
    }
}