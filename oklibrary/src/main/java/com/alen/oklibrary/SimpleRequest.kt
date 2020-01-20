package com.alen.oklibrary

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.headersContentLength
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 *  different request can use different okHttpClient,or use default okHttpClient
 */
class SimpleRequest(
    url: String,
    private val type: String
) {

    private var client: OkHttpClient = OkSimple.okHttpClient

    var requestCacheControl: CacheControl? = null

    private val paramsMap = hashMapOf<String, String>()

    private val postParamsMap = hashMapOf<String, String>()

    private val formParamsMap = hashMapOf<String, String>()

    /**
     * use the list because the key can be same
     */
    private val formFileKeyList = mutableListOf<String>()

    private val formFileList = mutableListOf<File>()

    private val formFileMediaTypeList = mutableListOf<MediaType>()

    private var requestUrl = url

    var tag = requestUrl

    private val requestBuilder = Request.Builder()

    private var postJSONObject = JSONObject()

    private lateinit var uploadFile: File

    internal var defaultFileMediaType = OkSimple.DEFAULT_MEDIA_TYPE_STRING.toMediaType()

    internal var fileName = ""

    internal var filePath = ""


    fun execute(callBack: ResultCallBack) {
        if (type == "downloadFile") {
            OkSimple.cachedThreadPool.execute {
                prepare(callBack)
                OkSimple.mainHandler.post {
                    process(callBack)
                }
            }
        } else {
            prepare(callBack)
            process(callBack)

        }
    }

    private fun  process(callBack: ResultCallBack) {
        val status = OkSimple.statusUrlMap[tag] ?: false
        if (OkSimple.preventContinuousRequests && status) {
            return
        }
        callBack.start()
        OkSimple.statusUrlMap[tag] = true
        val cache = requestCacheControl
        requestBuilder.url(requestUrl).tag(tag)
        if (OkSimple.networkUnavailableForceCache&&!OksimpleNetworkUtil.isNetworkAvailable()){
            requestBuilder.cacheControl(CacheControl.FORCE_CACHE)
        }else if (cache != null){
            requestBuilder.cacheControl(cache)
        }
        client.newCall(requestBuilder.build()).enqueue((object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                OkSimple.mainHandler.post {
                    callBack.failure(call, e)
                }
                OkSimple.statusUrlMap.remove(tag)
            }

            override fun onResponse(call: Call, response: Response) {
                callBack.response(call, response)
                OkSimple.statusUrlMap.remove(tag)
            }

        }))
    }

    fun setTheTag(tag: String) = apply {
        this.tag = tag
    }

    fun setTheRequestCacheControl(requestCacheControl: CacheControl) = apply {
        this.requestCacheControl = requestCacheControl
    }


    private fun appendParamsMapToUrl(map: Map<String, String>) {
        val stringBuilder = StringBuilder()
        stringBuilder.append(requestUrl)
        var index = 0
        val haveValueInUrl = requestUrl.indexOf("?") > 0 || requestUrl.indexOf("=") > 0
        for ((k, v) in map) {
            if (index == 0 && !haveValueInUrl) {
                stringBuilder.append("?")
            } else {
                stringBuilder.append("&")
            }
            stringBuilder.append(k)
            stringBuilder.append("=")
            stringBuilder.append(v)
            index++
        }
        requestUrl = stringBuilder.toString()
    }


    fun addHeader(key: String, value: String) = apply {
        requestBuilder.header(key, value)
    }

    /**
     * always append to url
     */
    fun params(key: String, value: String) = apply {
        paramsMap[key] = value
    }

    /**
     * only in post body
     */
    fun post(key: String, value: String) = apply {
        postParamsMap[key] = value
    }

    fun post(map: Map<String, String>) = apply {
        for ((k, v) in map) {
            postParamsMap[k] = v
        }
    }

    fun postJson(jsonObject: JSONObject) = apply {
        postJSONObject = jsonObject
    }

    fun uploadFile(file: File) = apply {
        uploadFile = file
    }

    fun addFormPart(key: String, value: String) = apply {
        formParamsMap[key] = value
    }


    fun addFormPart(
        key: String,
        file: File,
        mediaType: String = OkSimple.DEFAULT_MEDIA_TYPE_STRING
    ) = apply {
        formFileKeyList.add(key)
        formFileList.add(file)
        formFileMediaTypeList.add(mediaType.toMediaType())
    }

    internal fun  prepare(callBack: ResultCallBack): OkHttpClient {

        appendParamsMapToUrl(OkSimple.globalParamsMap)
        for ((k, v) in OkSimple.globalHeaderMap) {
            requestBuilder.header(k, v)
        }
        appendParamsMapToUrl(paramsMap)

        when (type) {

            "get" -> {

            }

            "post" -> {
                val formBody = FormBody.Builder()
                for ((k, v) in postParamsMap) {
                    formBody.add(k, v)
                }
                requestBuilder.post(formBody.build())

            }

            "postJson" -> {
                val requestBody =
                    postJSONObject.toString().toRequestBody("application/json".toMediaType())
                requestBuilder.post(requestBody)

            }

            "getBitmap" -> {
                val downloadBuilder = client.newBuilder()
                downloadBuilder.networkInterceptors().clear()
                downloadBuilder.interceptors().clear()
                downloadBuilder.addNetworkInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request()
                        val url = request.url.toString()
                        val originalResponse = chain.proceed(request)
                        val originalResponseBody = originalResponse.body
                        return if (originalResponseBody == null) originalResponse else originalResponse.newBuilder()
                            .body(ProgressResponseBody(url, originalResponseBody, callBack)).build()

                    }
                })
                client = downloadBuilder.build()
            }

            "downloadFile" -> {
                val downloadBuilder = client.newBuilder()
                downloadBuilder.networkInterceptors().clear()
                downloadBuilder.interceptors().clear()
                client = downloadBuilder.build()
                val file = File(filePath, fileName)
                val downloadLength = if (file.exists()) file.length() else 0
                val downloadResponse =
                    client.newCall(requestBuilder.url(requestUrl).build()).execute()
                val contentLength = downloadResponse.headersContentLength()
                val downloadBean=DownloadBean()
                downloadBean.url=requestUrl
                downloadBean.contentLength=contentLength
                downloadBean.downloadLength=downloadLength
                downloadBean.filePath=filePath
                downloadBean.filename=fileName
                callBack.urlToBeanMap[requestUrl]=downloadBean
                if (contentLength>0){
                    requestBuilder.addHeader("RANGE", "bytes=$downloadLength-$contentLength")
                }

            }

            "uploadFile" -> {
                val downloadBuilder = client.newBuilder()
                downloadBuilder.networkInterceptors().clear()
                downloadBuilder.interceptors().clear()
                client = downloadBuilder.build()
                requestBuilder.post(
                    ProgressRequestBody(
                        uploadFile.name,
                        uploadFile.asRequestBody(defaultFileMediaType),
                        callBack
                    )
                )
            }

            "postForm" -> {
                val downloadBuilder = client.newBuilder()
                downloadBuilder.networkInterceptors().clear()
                downloadBuilder.interceptors().clear()
                client = downloadBuilder.build()
                val multipartBodyBuilder = MultipartBody.Builder()
                multipartBodyBuilder.setType(MultipartBody.FORM)
                for ((k, v) in formParamsMap) {
                    multipartBodyBuilder.addFormDataPart(k, v)
                }
                formFileKeyList.forEachIndexed { index, s ->
                    val file = formFileList[index]
                    val fileName = file.name
                    multipartBodyBuilder.addFormDataPart(
                        s,
                        fileName,
                        ProgressRequestBody(
                            fileName,
                            file.asRequestBody(formFileMediaTypeList[index]),
                            callBack
                        )
                    )
                }
                requestBuilder.post(multipartBodyBuilder.build())
            }

        }

        return client
    }


}