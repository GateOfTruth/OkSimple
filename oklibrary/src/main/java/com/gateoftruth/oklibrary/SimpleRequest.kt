package com.gateoftruth.oklibrary

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
    url: String = "",
    private val type: String = OkSimpleConstant.GET
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

    private var requestBuilder = Request.Builder()

    private var postJSONObject = JSONObject()

    private lateinit var uploadFile: File

    internal var defaultFileMediaType = OkSimpleConstant.STREAM_MEDIA_TYPE_STRING.toMediaType()

    internal var fileName = ""

    internal var filePath = ""

    private var requestStrategy: RequestStrategy? = null

    fun setRequestStrategy(requestStrategy: RequestStrategy): SimpleRequest {
        this.requestStrategy = requestStrategy
        return this
    }


    fun execute(callBack: ResultCallBack) {
        var localVar = requestStrategy
        if (localVar == null) {
            localVar = DefaultStrategy()
        }
        localVar.strategyResultCallBack = callBack
        if (localVar.callStartFunction()) {
            callBack.start()
        }
        if (OkSimple.preventContinuousRequests) {
            val status = OkSimple.statusUrlMap[tag] ?: false
            if (status) {
                return
            } else {
                OkSimple.statusUrlMap[tag] = true
            }
        }
        if (type == OkSimpleConstant.DOWNLOAD_FILE) {
            OkSimple.cachedThreadPool.execute {
                prepare(callBack)
                process(localVar)
            }
        } else {
            prepare(callBack)
            process(localVar)
        }
    }


    internal fun process(strategy: RequestStrategy) {
        val localTag: String
        val callBack = strategy.strategyResultCallBack
        val strategyCall = strategy.strategyCall
        val localRequestBuilder: Request.Builder
        if (strategyCall != null) {
            localRequestBuilder = strategyCall.request().newBuilder()
            localTag = strategyCall.request().tag().toString()
        } else {
            localTag = tag
            val cache = requestCacheControl
            requestBuilder.url(requestUrl).tag(localTag)
            if (cache!=null){
                requestBuilder.cacheControl(cache)
            }else if (OkSimple.networkUnavailableForceCache && !OksimpleNetworkUtil.isNetworkAvailable()){
                requestBuilder.cacheControl(CacheControl.FORCE_CACHE)
            }
            localRequestBuilder = requestBuilder
        }
        val finalRequest = strategy.getRequestBuilder(localRequestBuilder).build()
        strategy.count++
        client.newCall(finalRequest).enqueue((object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (strategy.doResultCallBackFailure(call, e)) {
                    OkSimple.mainHandler.post {
                        callBack?.failure(call, e)
                    }
                }
                if (OkSimple.preventContinuousRequests) {
                    OkSimple.statusUrlMap.remove(localTag)
                }
                if (strategy.doRequestWhenOnFailure(call, e)) {
                    strategy.strategyCall = call
                    startRequestStrategy(strategy)
                    OkSimple.tagStrategyMap[localTag] = strategy
                } else {
                    OkSimple.tagStrategyMap.remove(localTag)
                }

            }

            override fun onResponse(call: Call, response: Response) {
                if (strategy.doResultCallBackResponse(call, response)) {
                    callBack?.response(call, response)
                }
                if (OkSimple.preventContinuousRequests) {
                    OkSimple.statusUrlMap.remove(localTag)
                }
                if (strategy.doRequestWhenOnResponse(call, response)) {
                    strategy.strategyCall = call
                    startRequestStrategy(strategy)
                    OkSimple.tagStrategyMap[localTag] = strategy
                } else {
                    OkSimple.tagStrategyMap.remove(localTag)
                }
            }

        }))
    }

    private fun startRequestStrategy(requestStrategy: RequestStrategy) {
        val message = OkSimple.mainHandler.obtainMessage()
        message.what = OkSimpleConstant.STRATEGY_MESSAGE
        message.obj = requestStrategy
        OkSimple.mainHandler.sendMessageDelayed(message, requestStrategy.delay())

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
        mediaType: String = OkSimpleConstant.STREAM_MEDIA_TYPE_STRING
    ) = apply {
        formFileKeyList.add(key)
        formFileList.add(file)
        formFileMediaTypeList.add(mediaType.toMediaType())
    }

    internal fun prepare(callBack: ResultCallBack): OkHttpClient {
        appendParamsMapToUrl(OkSimple.globalParamsMap)
        for ((k, v) in OkSimple.globalHeaderMap) {
            requestBuilder.header(k, v)
        }
        appendParamsMapToUrl(paramsMap)
        when (type) {

            OkSimpleConstant.GET -> {

            }

            OkSimpleConstant.POST -> {
                val formBody = FormBody.Builder()
                for ((k, v) in postParamsMap) {
                    formBody.add(k, v)
                }
                requestBuilder.post(formBody.build())

            }

            OkSimpleConstant.POST_JSON -> {
                val requestBody =
                    postJSONObject.toString()
                        .toRequestBody(OkSimpleConstant.JSON_MEDIA_TYPE_STRING.toMediaType())
                requestBuilder.post(requestBody)

            }

            OkSimpleConstant.GET_BITMAP -> {
                val bitmapBuilder = client.newBuilder()
                val interceptors = bitmapBuilder.interceptors()
                interceptors.add(0, object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request()
                        val url = request.url.toString()
                        val originalResponse = chain.proceed(request)
                        val originalResponseBody = originalResponse.body
                        return if (originalResponseBody == null) originalResponse else originalResponse.newBuilder()
                            .body(ProgressResponseBody(url, originalResponseBody, callBack)).build()

                    }
                })
                client = bitmapBuilder.build()
            }

            OkSimpleConstant.DOWNLOAD_FILE -> {
                val file = File(filePath, fileName)
                val downloadLength = if (file.exists()) file.length() else 0
                val downloadBean = DownloadBean()
                var contentLength = 0L
                downloadBean.url = requestUrl
                try {
                    val headRequest = requestBuilder.url(requestUrl).head().build()
                    val downloadResponse =
                        client.newCall(headRequest).execute()
                    contentLength = downloadResponse.headersContentLength()
                    requestBuilder = headRequest.newBuilder().get()
                    downloadBean.contentLength = contentLength
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                downloadBean.downloadLength = downloadLength
                downloadBean.filePath = filePath
                downloadBean.filename = fileName
                callBack.urlToBeanMap[requestUrl] = downloadBean
                if (contentLength > 0) {
                    requestBuilder.addHeader("RANGE", "bytes=$downloadLength-$contentLength")
                }

            }

            OkSimpleConstant.UPLOAD_FILE -> {
                requestBuilder.post(
                    ProgressRequestBody(
                        uploadFile.name,
                        uploadFile.asRequestBody(defaultFileMediaType),
                        callBack
                    )
                )
            }

            OkSimpleConstant.POST_FORM -> {
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