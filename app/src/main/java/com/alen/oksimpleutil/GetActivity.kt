package com.alen.oksimpleutil

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alen.oklibrary.OkSimple
import kotlinx.android.synthetic.main.activity_get.*
import okhttp3.Call
import okhttp3.Response

class GetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get)
        val dialog=AlertDialog.Builder(this@GetActivity).setTitle("dialog").setMessage("request").create()
        OkSimple.addGlobalHeader("testGlobalHeader","3")
        OkSimple.get("http://api.juheapi.com/japi/toh") .params("month","1")
            .params("day","1")
            .params("key","a4a8acd821a6412a361310249f085d96").execute(object :GsonCallBack<ExampleBean>(){
                override fun start() {
                    dialog.show()
                }
                override fun getData(
                    data: ExampleBean,
                    rawBodyString: String,
                    call: Call,
                    response: Response
                ) {
                    dialog.dismiss()
                    tv_result.text=data.result.component1().title
                }

                override fun failure(call: Call, e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }

            })

    }
}
