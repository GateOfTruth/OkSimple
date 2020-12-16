package com.gateoftruth.oklibrary

import android.graphics.Bitmap
import okhttp3.Call
import okhttp3.Response

open class GlideCallBack : BitmapResultCallBack() {
    override fun finish(bitmap: Bitmap) {

    }

    override fun failure(call: Call, e: Exception) {

    }

    override fun response(call: Call, response: Response) {

    }

    override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {

    }

    override fun downloadProgress(url: String, total: Long, current: Long) {

    }

}