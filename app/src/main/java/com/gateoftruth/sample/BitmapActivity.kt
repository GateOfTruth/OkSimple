package com.gateoftruth.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gateoftruth.oklibrary.BitmapResultCallBack
import com.gateoftruth.oklibrary.OkSimple
import com.gateoftruth.sample.databinding.ActivityBitmapBinding
import okhttp3.Call

class BitmapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityBitmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val theUrl=""
        if (!theUrl.startsWith("http")){
            Toast.makeText(this,"请先在代码里设置url", Toast.LENGTH_LONG).show()
            return
        }
        OkSimple.getBitmap(theUrl).execute(object : BitmapResultCallBack() {
            override fun finish(bitmap: Bitmap) {
                binding.ivBitmap.setImageBitmap(bitmap)

            }

            override fun failure(call: Call, e: Exception) {

            }

        })
    }
}
