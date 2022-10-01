package com.gateoftruth.oklibrary

import okhttp3.Call
import okhttp3.Response
import java.net.ConnectException

abstract class NewDataCallBack<E>(private val isMainThread: Boolean = true) : ResultCallBack() {
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
        if (response.code == 200) {
            val rawString = responseBody.string()
            val data = stringToData(rawString)
            if (isMainThread) {
                OkSimple.mainHandler.post {
                    returnTheData(data, rawString, call, response)
                }
            } else {
                returnTheData(data, rawString, call, response)
            }
        } else {
            otherException(call, response, Exception("2000"))
        }
    }

    open fun returnTheData(data: E?, rawString: String, call: Call, response: Response) {
        if (data != null) {
            getData(data, rawString, call, response)
        } else {
            otherException(
                call,
                response,
                Exception("2001")
            )
        }
    }

    abstract fun stringToData(string: String): E?

    abstract fun getData(data: E, rawBodyString: String, call: Call, response: Response)

    override fun responseBodyGetNull(call: Call, response: Response) {

    }

    override fun downloadProgress(url: String, total: Long, current: Long) {

    }

    override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {

    }

    override fun uploadProgress(fileName: String, total: Long, current: Long) {

    }

    override fun uploadProgressOnMainThread(fileName: String, total: Long, current: Long) {

    }

}