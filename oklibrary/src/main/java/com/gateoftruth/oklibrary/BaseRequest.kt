package com.gateoftruth.oklibrary

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.toLongOrDefault
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

abstract class BaseRequest(val url: String, val type: String) {
    protected var client: OkHttpClient = OkSimple.okHttpClient

    var requestCacheControl: CacheControl? = null

    protected val paramsMap = hashMapOf<String, String>()

    protected val postParamsMap = hashMapOf<String, String>()

    protected val formParamsMap = hashMapOf<String, String>()

    protected val formFileKeyList = mutableListOf<String>()

    protected val formFileList = mutableListOf<File>()

    protected val formFileMediaTypeList = mutableListOf<MediaType>()

    protected var requestUrl = url

    var tag = requestUrl

    protected var requestBuilder = Request.Builder()

    protected var postJSONObject = JSONObject()

    protected var postJSONArray = JSONArray()

    protected lateinit var uploadFile: File

    internal var defaultFileMediaType = OkSimpleConstant.STREAM_MEDIA_TYPE_STRING.toMediaType()

    internal var fileName = ""

    internal var filePath = ""

    protected var requestStrategy: RequestStrategy? = null

    protected var methodName = OkSimpleConstant.GET

    protected var methodRequestBody: RequestBody? = null

    protected var contentString = ""

    protected var specialRequest = false


    fun setRequestStrategy(requestStrategy: RequestStrategy): BaseRequest {
        this.requestStrategy = requestStrategy
        return this
    }

    internal fun prepare(callBack: ResultCallBack?) {
        appendParamsMapToUrl(OkSimple.globalParamsMap)
        for ((k, v) in OkSimple.globalHeaderMap) {
            requestBuilder.header(k, v)
        }
        appendParamsMapToUrl(paramsMap)
        when (type) {

            OkSimpleConstant.GET -> {
                contentString = "type:get;"
                requestBuilder.get()
            }

            OkSimpleConstant.POST -> {
                val formBody = FormBody.Builder()
                val stringBuilder = StringBuilder()
                stringBuilder.append("type:post;")
                for ((k, v) in postParamsMap) {
                    formBody.add(k, v)
                    stringBuilder.append(k)
                    stringBuilder.append("=")
                    stringBuilder.append(v)
                }
                contentString = stringBuilder.toString()
                requestBuilder.post(formBody.build())

            }

            OkSimpleConstant.POST_JSON -> {
                stringPost(postJSONObject.toString(),OkSimpleConstant.POST_JSON)
            }

            OkSimpleConstant.POST_JSON_ARRAY -> {
                stringPost(postJSONArray.toString(),OkSimpleConstant.POST_JSON_ARRAY)
            }

            OkSimpleConstant.GET_BITMAP -> {
                contentString = "type:GET_BITMAP;"
                requestBuilder.get()
            }

            OkSimpleConstant.DOWNLOAD_FILE -> {
                val file = File(filePath, fileName)
                val downloadLength = if (file.exists()) file.length() else 0
                val downloadBean = DownloadBean()
                var contentLength = 0L
                var acceptRange: String? = null
                downloadBean.url = requestUrl
                try {
                    val headRequest = requestBuilder.url(requestUrl).head().build()
                    val downloadResponse =
                        client.newCall(headRequest).execute()
                    contentLength = downloadResponse.headers["Content-Length"]?.toLongOrDefault(-1L) ?: -1L
                    acceptRange = downloadResponse.headers["accept-ranges"]
                    requestBuilder = headRequest.newBuilder().get()
                    downloadBean.contentLength = contentLength
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                downloadBean.downloadLength = downloadLength
                downloadBean.filePath = filePath
                downloadBean.filename = fileName
                if (callBack != null) {
                    callBack.urlToBeanMap[requestUrl] = downloadBean
                }
                if (!acceptRange.isNullOrEmpty() && acceptRange != "none" && contentLength > 0) {
                    requestBuilder.addHeader("RANGE", "bytes=$downloadLength-$contentLength")
                }
                contentString = "type:DOWNLOAD_FILE;"
            }

            OkSimpleConstant.UPLOAD_FILE -> {
                contentString = "type:UPLOAD_FILE;${uploadFile.name}"
                requestBuilder.post(
                    ProgressRequestBody(
                        uploadFile.name,
                        uploadFile.asRequestBody(defaultFileMediaType),
                        callBack
                    )
                )
            }

            OkSimpleConstant.POST_FORM -> {
                val stringBuilder = StringBuilder()
                stringBuilder.append("type:POST_FORM;")
                val multipartBodyBuilder = MultipartBody.Builder()
                multipartBodyBuilder.setType(MultipartBody.FORM)
                for ((k, v) in formParamsMap) {
                    stringBuilder.append(k)
                    stringBuilder.append("=")
                    stringBuilder.append(v)
                    multipartBodyBuilder.addFormDataPart(k, v)
                }
                stringBuilder.append(";")
                formFileKeyList.forEachIndexed { index, s ->
                    val file = formFileList[index]
                    val fileName = file.name
                    stringBuilder.append("POST_FORM File Name=")
                    stringBuilder.append(file.name)
                    stringBuilder.append(";")
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
                contentString = stringBuilder.toString()
                requestBuilder.post(multipartBodyBuilder.build())
            }

            OkSimpleConstant.METHOD -> {
                specialRequest = true
                requestBuilder.method(methodName, methodRequestBody)
            }
        }

    }

    private fun stringPost(string: String,type:String){
        val requestBody = string.toRequestBody(OkSimpleConstant.JSON_MEDIA_TYPE_STRING.toMediaType())
        contentString = "type:$type;$string"
        requestBuilder.post(requestBody)
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

    fun setTheTag(tag: String) = apply {
        this.tag = tag
    }

    fun setTheRequestCacheControl(requestCacheControl: CacheControl) = apply {
        this.requestCacheControl = requestCacheControl
    }

    fun addHeader(key: String, value: String) = apply {
        requestBuilder.header(key, value)
    }

    fun params(key: String, value: String) = apply {
        paramsMap[key] = value
    }


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

    fun postJsonArray(jsonArray: JSONArray) = apply {
        postJSONArray = jsonArray
    }

    fun uploadFile(file: File) = apply {
        uploadFile = file
    }

    fun method(methodName: String, requestBody: RequestBody?) = apply {
        this.methodName = methodName
        methodRequestBody = requestBody
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

    abstract fun execute(callBack: ResultCallBack)

    abstract fun <T> execute(bean: BaseSynchronizeBean<T>): BaseSynchronizeBean<T>


}