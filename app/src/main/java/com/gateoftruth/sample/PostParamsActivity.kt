package com.gateoftruth.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.gateoftruth.oklibrary.OkSimple
import kotlinx.android.synthetic.main.activity_post_params.*
import okhttp3.Call
import okhttp3.Response

class PostParamsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_params)
        btn_with_map.setOnClickListener {
            val map = hashMapOf<String, String>()
            map["v"] = "1"
            map["month"] = "1"
            map["day"] = "1"
            OkSimple.post("post url").post(map)
                .params("key", "value")
                .execute(object : GsonCallBack<ExampleBean>() {
                    override fun getData(
                        data: ExampleBean,
                        rawBodyString: String,
                        call: Call,
                        response: Response
                    ) {
                        Toast.makeText(
                            this@PostParamsActivity,
                            data.result[0].des,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun failure(call: Call, e: Exception) {
                        Toast.makeText(this@PostParamsActivity, "failure2", Toast.LENGTH_SHORT)
                            .show()
                    }

                })
        }

        btn_without_map.setOnClickListener {
            OkSimple.post("http://api.juheapi.com/japi/toh")
                .post("v", "1")
                .post("month", "1")
                .post("day", "1")
                .post("key", "a4a8acd821a6412a361310249f085d96").execute(object :
                    GsonCallBack<ExampleBean>() {
                    override fun getData(
                        data: ExampleBean,
                        rawBodyString: String,
                        call: Call,
                        response: Response
                    ) {
                        Toast.makeText(
                            this@PostParamsActivity,
                            data.result[0].des,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun failure(call: Call, e: Exception) {
                        Toast.makeText(this@PostParamsActivity, "failure", Toast.LENGTH_SHORT)
                            .show()
                    }

                })
        }


    }
}
