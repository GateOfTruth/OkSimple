package com.gateoftruth.oklibrary

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.Call
import okhttp3.Response

abstract class BitmapResultCallBack(private val isMainThread: Boolean = true) : ResultCallBack() {

    override fun start() {

    }

    override fun response(call: Call, response: Response) {
        val responseBody = response.body
        if (responseBody == null) {
            if (isMainThread) {
                OkSimple.mainHandler.post {
                    responseBodyGetNull(call, response)
                }
            } else {
                responseBodyGetNull(call, response)
            }
            return
        }
        try {
            val inputStream = responseBody.byteStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (isMainThread) {
                OkSimple.mainHandler.post {
                    returnTheBitmap(bitmap, call, response)
                }
            } else {
                returnTheBitmap(bitmap, call, response)
            }

        } catch (e: Exception) {
            if (isMainThread) {
                OkSimple.mainHandler.post {
                    otherException(call, response, e)
                }
            } else {
                otherException(call, response, e)
            }

        }


    }

    open fun returnTheBitmap(bitmap: Bitmap?, call: Call, response: Response) {
        if (bitmap == null) {
            otherException(
                call,
                response,
                IllegalArgumentException("BitmapFactory.decodeStream() function get null")
            )
        } else {
            finish(bitmap)
        }
    }

    override fun otherException(call: Call, response: Response, e: Exception) {
        failure(call, e)
    }

    override fun responseBodyGetNull(call: Call, response: Response) {

    }


    abstract fun finish(bitmap: Bitmap)


    override fun downloadProgress(url: String, total: Long, current: Long) {
        OkSimple.mainHandler.post {
            downloadProgressOnMainThread(url, total, current)
        }
    }

    override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {

    }

    override fun uploadProgress(fileName: String, total: Long, current: Long) {

    }

    override fun uploadProgressOnMainThread(fileName: String, total: Long, current: Long) {

    }
}