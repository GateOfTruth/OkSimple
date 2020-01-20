package com.alen.oklibrary

import android.app.Application
import android.os.Handler
import android.os.Looper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object OkSimple {
    var okHttpClient = OkHttpClient()

    val mainHandler = Handler(Looper.getMainLooper())

    val globalHeaderMap = hashMapOf<String, String>()

    val globalParamsMap = hashMapOf<String, String>()

    var preventContinuousRequests=true

    var networkUnavailableForceCache=true

    val statusUrlMap= ConcurrentHashMap<String,Boolean>()

    const val DEFAULT_MEDIA_TYPE_STRING = "application/octet-stream"

    val cachedThreadPool: ExecutorService = Executors.newCachedThreadPool()

    var networkAvailable=true

    var application: Application?=null
    set(value) {
        field=value
        if (value!=null){
            OksimpleNetworkUtil.init(value)
        }
    }


    fun addGlobalHeader(key: String, value: String) {
        globalHeaderMap[key] = value
    }

    /**
     * append to url
     */
    fun addGlobalParams(key: String, value: String) {
        globalParamsMap[key] = value
    }

    fun  get(url: String): SimpleRequest {
        return SimpleRequest(url,"get")
    }

    fun  postJson(url: String, jsonObject: JSONObject): SimpleRequest {
        val request = SimpleRequest(url,"postJson")
        request.postJson(jsonObject)
        return request
    }

    fun  post(url: String, valueMap: Map<String, String> = HashMap()): SimpleRequest {
        val request = SimpleRequest(url,"post")
        request.post(valueMap)
        return request
    }

    fun downloadFile(url: String,filename: String,filePath: String): SimpleRequest {
        val request=SimpleRequest(url,"downloadFile")
        request.fileName=filename
        request.filePath=filePath
        return request
    }

    fun getBitmap(url: String): SimpleRequest {
        return SimpleRequest(url,"getBitmap")
    }

    fun  uploadFile(url: String,file: File,mediaType: String="application/octet-stream"):SimpleRequest{
        val request=SimpleRequest(url,"uploadFile")
        request.uploadFile(file)
        request.defaultFileMediaType=mediaType.toMediaType()
        return request
    }

    fun  postForm(url: String):SimpleRequest{
        return SimpleRequest(url,"postForm")
    }

    fun <G: GlideCallBack> getGlideClient(listener:G):OkHttpClient{
        return  getBitmap("").prepare(listener)
    }


    fun cancelCall(tag:String){
        val queuedCall=okHttpClient.dispatcher.queuedCalls().firstOrNull{
            it.request().tag().toString()==tag
        }
        queuedCall?.cancel()

        val runningCall=okHttpClient.dispatcher.runningCalls().firstOrNull {
            it.request().tag().toString()==tag
        }
        runningCall?.cancel()
        statusUrlMap.remove(tag)
    }

    fun cancelAll(){
        okHttpClient.dispatcher.cancelAll()
        statusUrlMap.clear()
    }



}