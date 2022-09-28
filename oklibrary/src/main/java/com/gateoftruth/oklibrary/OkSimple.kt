package com.gateoftruth.oklibrary

import android.app.Application
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

object OkSimple {
    var okHttpClient = OkHttpClient()

    val mainHandler = OksimpleHandler()

    val globalHeaderMap = hashMapOf<String, String>()

    val globalParamsMap = hashMapOf<String, String>()

    var preventContinuousRequests = false

    var networkUnavailableForceCache = true

    internal val statusSet = Collections.newSetFromMap(ConcurrentHashMap<RequestObject, Boolean>())

    internal val tagStrategyMap = ConcurrentHashMap<String, RequestStrategy>()

    var networkAvailable = true

    var application: Application? = null
        set(value) {
            field = value
            if (value != null) {
                OksimpleNetworkUtil.init(value)
            }
        }


    fun addGlobalHeader(key: String, value: String) {
        globalHeaderMap[key] = value
    }

    fun addGlobalParams(key: String, value: String) {
        globalParamsMap[key] = value
    }

    fun get(url: String, isSync: Boolean = false): BaseRequest {
        return if (isSync) SynchronizeRequest(url, OkSimpleConstant.GET) else AsynchronousRequest(
            url,
            OkSimpleConstant.GET
        )
    }

    fun postJson(url: String, jsonObject: JSONObject, isSync: Boolean = false): BaseRequest {
        val request = if (isSync) SynchronizeRequest(
            url,
            OkSimpleConstant.POST_JSON
        ) else AsynchronousRequest(url, OkSimpleConstant.POST_JSON)
        request.postJson(jsonObject)
        return request
    }

    fun postJson(url: String, jsonArray: JSONArray, isSync: Boolean = false): BaseRequest {
        val request = if (isSync) SynchronizeRequest(
            url,
            OkSimpleConstant.POST_JSON_ARRAY
        ) else AsynchronousRequest(url, OkSimpleConstant.POST_JSON_ARRAY)
        request.postJsonArray(jsonArray)
        return request
    }

    fun post(
        url: String,
        valueMap: Map<String, String> = HashMap(),
        isSync: Boolean = false
    ): BaseRequest {
        val request =
            if (isSync) SynchronizeRequest(url, OkSimpleConstant.POST) else AsynchronousRequest(
                url,
                OkSimpleConstant.POST
            )
        request.post(valueMap)
        return request
    }

    fun downloadFile(
        url: String,
        filename: String,
        filePath: String,
        isSync: Boolean = false
    ): BaseRequest {
        val request = if (isSync) SynchronizeRequest(
            url,
            OkSimpleConstant.DOWNLOAD_FILE
        ) else AsynchronousRequest(url, OkSimpleConstant.DOWNLOAD_FILE)
        request.fileName = filename
        request.filePath = filePath
        return request
    }

    fun getBitmap(url: String, isSync: Boolean = false): BaseRequest {
        return if (isSync) SynchronizeRequest(
            url,
            OkSimpleConstant.GET_BITMAP
        ) else AsynchronousRequest(url, OkSimpleConstant.GET_BITMAP)
    }

    fun uploadFile(
        url: String,
        file: File,
        mediaType: String = OkSimpleConstant.STREAM_MEDIA_TYPE_STRING, isSync: Boolean = false
    ): BaseRequest {
        val request = if (isSync) SynchronizeRequest(
            url,
            OkSimpleConstant.UPLOAD_FILE
        ) else AsynchronousRequest(url, OkSimpleConstant.UPLOAD_FILE)
        request.uploadFile(file)
        request.defaultFileMediaType = mediaType.toMediaType()
        return request
    }

    fun postForm(url: String, isSync: Boolean = false): BaseRequest {
        return if (isSync) SynchronizeRequest(
            url,
            OkSimpleConstant.POST_FORM
        ) else AsynchronousRequest(url, OkSimpleConstant.POST_FORM)
    }

    fun method(
        url: String,
        methodName: String,
        requestBody: RequestBody? = null,
        isSync: Boolean = false
    ): BaseRequest {
        val request =
            if (isSync) SynchronizeRequest(url, OkSimpleConstant.METHOD) else AsynchronousRequest(
                url,
                OkSimpleConstant.METHOD
            )
        request.method(methodName, requestBody)
        return request
    }

    fun <G : GlideCallBack> getGlideClient(listener: G): OkHttpClient {
        val bitmapBuilder = okHttpClient.newBuilder()
        val interceptors = bitmapBuilder.interceptors()
        interceptors.add(0, Interceptor { chain ->
            val request = chain.request()
            val url = request.url.toString()
            val originalResponse = chain.proceed(request)
            val originalResponseBody = originalResponse.body
            if (originalResponseBody == null) originalResponse else originalResponse.newBuilder()
                .body(ProgressResponseBody(url, originalResponseBody, listener)).build()
        })
        return bitmapBuilder.build()
    }

    internal fun strategyRequest(strategy: RequestStrategy) {
        AsynchronousRequest("", "").process(strategy)
    }


    fun cancelCall(tag: String) {
        val runningCall = okHttpClient.dispatcher.runningCalls().firstOrNull {
            it.request().tag().toString() == tag
        }
        runningCall?.cancel()

        val strategy = tagStrategyMap[tag]
        if (strategy != null) {
            mainHandler.removeMessages(OkSimpleConstant.STRATEGY_MESSAGE, strategy)
        }

        val queuedCall = okHttpClient.dispatcher.queuedCalls().firstOrNull {
            it.request().tag().toString() == tag
        }
        queuedCall?.cancel()
    }

    fun cancelAll() {
        okHttpClient.dispatcher.cancelAll()
        statusSet.clear()
        mainHandler.removeMessages(OkSimpleConstant.STRATEGY_MESSAGE)
    }


}