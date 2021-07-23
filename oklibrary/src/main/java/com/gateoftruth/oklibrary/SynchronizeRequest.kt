package com.gateoftruth.oklibrary

import okhttp3.CacheControl
import okhttp3.Response

class SynchronizeRequest(url: String, type: String) : BaseRequest(url, type) {

    override fun <T> execute(bean: BaseSynchronizeBean<T>?): BaseSynchronizeBean<T> {
        val resultBean = bean ?: NormalSynchronizeBean()
        try {
            if (OkSimple.preventContinuousRequests) {
                val status = OkSimple.statusUrlMap[tag] ?: false
                if (status) {
                    resultBean.exception =
                        RuntimeException("The request has been intercepted because it is a duplicate request")
                } else {
                    OkSimple.statusUrlMap[tag] = true
                    prepare(null)
                    resultBean.response = processSynchronize()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            resultBean.exception = e
        } finally {
            if (OkSimple.preventContinuousRequests) {
                OkSimple.statusUrlMap.remove(tag)
            }
            return resultBean
        }
    }

    private fun processSynchronize(): Response {
        val cache = requestCacheControl
        requestBuilder.url(requestUrl).tag(tag)
        if (cache != null) {
            requestBuilder.cacheControl(cache)
        } else if (OkSimple.networkUnavailableForceCache && !OksimpleNetworkUtil.isNetworkAvailable()) {
            requestBuilder.cacheControl(CacheControl.FORCE_CACHE)
        }
        return client.newCall(requestBuilder.build()).execute()
    }


    override fun execute(callBack: ResultCallBack) {

    }
}