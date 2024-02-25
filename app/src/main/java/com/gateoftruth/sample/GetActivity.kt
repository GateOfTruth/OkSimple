package com.gateoftruth.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.gateoftruth.oklibrary.OkSimple
import com.gateoftruth.sample.databinding.ActivityGetBinding
import okhttp3.Call
import okhttp3.Response

class GetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityGetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dialog =
            AlertDialog.Builder(this@GetActivity).setTitle("dialog").setMessage("request").create()
        OkSimple.addGlobalHeader("testGlobalHeader", "3")
        val theUrl=""
        if (!theUrl.startsWith("http")){
            Toast.makeText(this,"请先在代码里设置url", Toast.LENGTH_LONG).show()
            return
        }
        OkSimple.get(theUrl).setRequestStrategy(ChangeHostStrategy())
            .params("key", "value")
            .params("key", "value")
            .params("key", "value").execute(object :
                GsonCallBack<ExampleBean>() {
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
                    binding.tvResult.text = data.result.component1().title
                }

                override fun failure(call: Call, e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }

            })

    }

}
