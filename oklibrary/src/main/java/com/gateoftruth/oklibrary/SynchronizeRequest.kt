package com.gateoftruth.oklibrary

import okhttp3.CacheControl
import okhttp3.Request
import okhttp3.Response

class SynchronizeRequest(url: String, type: String) : BaseRequest(url, type) {

    lateinit var requestObject: RequestObject
    override fun <T> execute(bean: BaseSynchronizeBean<T>): BaseSynchronizeBean<T> {
        try {
            val cache = requestCacheControl
            requestBuilder.url(requestUrl).tag(tag)
            if (cache != null) {
                requestBuilder.cacheControl(cache)
            } else if (OkSimple.networkUnavailableForceCache && !OksimpleNetworkUtil.isNetworkAvailable()) {
                requestBuilder.cacheControl(CacheControl.FORCE_CACHE)
            }
            prepare(null)
            val finalRequest = requestBuilder.build()
            requestObject =
                RequestObject(tag, contentString, finalRequest.body?.contentLength() ?: -1L)
            if (OkSimple.preventContinuousRequests && !specialRequest) {
                val hasSameRequest = OkSimple.statusSet.contains(requestObject)
                if (hasSameRequest) {
                    bean.exception =
                        RuntimeException("${OkSimpleConstant.OKSIMPLE_TAG}:Same Request!!! This request has been abandoned!!!,${requestObject}")
                } else {
                    OkSimple.statusSet.add(requestObject)
                    bean.response = processSynchronize(finalRequest)
                }
            } else {
                bean.response = processSynchronize(finalRequest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            bean.exception = e
        } finally {
            if (OkSimple.preventContinuousRequests) {
                OkSimple.statusSet.remove(requestObject)
            }
        }
        return bean
    }

    private fun processSynchronize(finalRequest: Request): Response {
        return client.newCall(finalRequest).execute()
    }


    override fun execute(callBack: ResultCallBack) {

    }
}