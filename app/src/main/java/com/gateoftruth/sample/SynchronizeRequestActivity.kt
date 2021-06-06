package com.gateoftruth.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.gateoftruth.oklibrary.OkSimple

class SynchronizeRequestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synchronize_request)
        val thread=object :Thread(){
            override fun run() {
                val response=OkSimple.get("http://api.juheapi.com/japi/toh")
                    .params("month", "1")
                    .params("day", "1")
                    .params("key", "a4a8acd821a6412a361310249f085d96").executeSynchronize().response
                if (response!=null){
                    val s=response.body?.string()
                    Log.e("body",s?:"")
                }
            }
        }
        thread.start()
    }
}