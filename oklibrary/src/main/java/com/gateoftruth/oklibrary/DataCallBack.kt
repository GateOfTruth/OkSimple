package com.gateoftruth.oklibrary

import okhttp3.Call
import okhttp3.Response


abstract class DataCallBack<E>(private val isMainThread: Boolean = true) : ResultCallBack() {

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
            val rawString = responseBody.string()
            val data = stringToData(preProcessBodyString(rawString))
            if (isMainThread) {
                OkSimple.mainHandler.post {
                    returnTheData(data, rawString, call, response)
                }
            } else {
                returnTheData(data, rawString, call, response)
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

    open fun returnTheData(data: E, rawString: String, call: Call, response: Response) {
        if (data != null) {
            getData(data, rawString, call, response)
        } else {
            otherException(
                call,
                response,
                IllegalArgumentException("stringToData() function get null")
            )
        }
    }

    abstract fun stringToData(string: String): E

    open fun preProcessBodyString(bodyString: String): String {
        return bodyString
    }

    override fun otherException(call: Call, response: Response, e: Exception) {
        failure(call, e)
    }

    override fun downloadProgress(url: String, total: Long, current: Long) {

    }

    override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {

    }

    override fun uploadProgress(fileName: String, total: Long, current: Long) {
        OkSimple.mainHandler.post {
            uploadProgressOnMainThread(fileName, total, current)
        }
    }

    override fun uploadProgressOnMainThread(fileName: String, total: Long, current: Long) {

    }

    override fun responseBodyGetNull(call: Call, response: Response) {
        failure(call, RuntimeException("responseBodyGetNull"))
    }


    abstract fun getData(data: E, rawBodyString: String, call: Call, response: Response)


}