package com.gateoftruth.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.gateoftruth.oklibrary.OkSimple
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject

class PostJsonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_json)
        val tem = JSONObject()
        tem.put("key1", "value1")
        tem.put("key2", "value2")
        tem.put("key3", "value3")
        OkSimple.postJson("post json url", tem).execute(object : GsonCallBack<Any>() {
            override fun getData(
                data: Any,
                rawBodyString: String,
                call: Call,
                response: Response
            ) {

            }

            override fun failure(call: Call, e: Exception) {

            }

        })
    }
}
