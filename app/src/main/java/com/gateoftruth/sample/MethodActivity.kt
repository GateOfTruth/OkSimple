package com.gateoftruth.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gateoftruth.oklibrary.OkSimple
import com.gateoftruth.oklibrary.OkSimpleConstant
import okhttp3.Call
import okhttp3.Response

class MethodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_method)
        OkSimple.method("https://www.baidu.com",OkSimpleConstant.HEAD).execute(object :GsonCallBack<ExampleBean>(){
            override fun getData(
                data: ExampleBean,
                rawBodyString: String,
                call: Call,
                response: Response
            ) {

            }

            override fun failure(call: Call, e: Exception) {

            }

        } )

    }
}