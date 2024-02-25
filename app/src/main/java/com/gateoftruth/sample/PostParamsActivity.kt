package com.gateoftruth.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.gateoftruth.oklibrary.OkSimple
import com.gateoftruth.sample.databinding.ActivityPostParamsBinding
import okhttp3.Call
import okhttp3.Response

class PostParamsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityPostParamsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnWithMap.setOnClickListener {
            val theUrl=""
            if (!theUrl.startsWith("http")){
                Toast.makeText(this,"请先在代码里设置url", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val map = hashMapOf<String, String>()
            map["v"] = "1"
            map["month"] = "1"
            map["day"] = "1"
            OkSimple.post(theUrl).post(map)
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

        binding.btnWithoutMap.setOnClickListener {
            val theUrl=""
            if (!theUrl.startsWith("http")){
                Toast.makeText(this,"请先在代码里设置url", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            OkSimple.post(theUrl)
                .post("v", "1")
                .post("month", "1")
                .post("day", "1")
                .post("key", "value").execute(object :
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
