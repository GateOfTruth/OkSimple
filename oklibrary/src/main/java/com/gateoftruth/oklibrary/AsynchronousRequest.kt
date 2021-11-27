package com.gateoftruth.oklibrary

import android.util.Log
import okhttp3.*
import java.io.IOException

class AsynchronousRequest(url: String, type: String) : BaseRequest(url, type) {

    override fun execute(callBack: ResultCallBack) {
        var localVar = requestStrategy
        if (localVar == null) {
            localVar = DefaultStrategy()
        }
        localVar.strategyResultCallBack = callBack
        if (type == OkSimpleConstant.DOWNLOAD_FILE) {
            OkSimple.okHttpClient.dispatcher.executorService.execute {
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
            if (cache != null) {
                requestBuilder.cacheControl(cache)
            } else if (OkSimple.networkUnavailableForceCache && !OksimpleNetworkUtil.isNetworkAvailable()) {
                requestBuilder.cacheControl(CacheControl.FORCE_CACHE)
            }
            localRequestBuilder = requestBuilder
        }
        val finalRequest = strategy.getRequestBuilder(localRequestBuilder).build()
        val requestObject =
            RequestObject(localTag, contentString, finalRequest.body?.contentLength() ?: -1L)
        if (OkSimple.preventContinuousRequests && !specialRequest) {
            val hasSameRequest = OkSimple.statusSet.contains(requestObject)
            if (hasSameRequest) {
                callBack?.requestAbandon(finalRequest,requestObject)
                return
            } else {
                OkSimple.statusSet.add(requestObject)
            }
        }
        if (strategy.callBackStart()) {
            callBack?.start()
        }
        strategy.count++
        client.newCall(finalRequest).enqueue((object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (strategy.callBackFailure(call, e)) {
                    OkSimple.mainHandler.post {
                        callBack?.failure(call, e)
                    }
                }
                if (OkSimple.preventContinuousRequests) {
                    OkSimple.statusSet.remove(requestObject)
                }
                if (strategy.requestAgainOnFailure(call, e)) {
                    strategy.strategyCall = call
                    startRequestStrategy(strategy)
                    OkSimple.tagStrategyMap[localTag] = strategy
                } else {
                    OkSimple.tagStrategyMap.remove(localTag)
                }

            }

            override fun onResponse(call: Call, response: Response) {
                if (strategy.callBackResponse(call, response)) {
                    callBack?.response(call, response)
                }
                if (OkSimple.preventContinuousRequests) {
                    OkSimple.statusSet.remove(requestObject)
                }
                if (strategy.requestAgainOnResponse(call, response)) {
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
        OkSimple.mainHandler.sendMessageDelayed(message, requestStrategy.requestDelay())
    }

    override fun <T> execute(bean: BaseSynchronizeBean<T>): BaseSynchronizeBean<T> {
        return NormalSynchronizeBean()
    }
}