package com.gateoftruth.oklibrary

import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 只要请求失败，就一直请求，直到成功
 */
open class RequestUntilSuccess : RequestStrategy() {
    override fun requestAgainOnResponse(call: Call, response: Response): Boolean {
        return false
    }

    override fun callBackResponse(call: Call, response: Response): Boolean {
        return true
    }

    override fun callBackFailure(call: Call, e: IOException): Boolean {
        return true
    }

    override fun requestAgainOnFailure(call: Call, e: IOException): Boolean {
        return true
    }

    override fun getRequestBuilder(builder: Request.Builder): Request.Builder {
        return builder
    }

    /**
     * 失败之后每隔五秒请求一次
     */
    override fun requestDelay(): Long {
        return 5000
    }

    override fun maxRequestTimes(): Long {
        return Long.MAX_VALUE
    }

    /**
     * 仅第一次ResultCallback调用start()函数
     */
    override fun callBackStart(): Boolean {
        return count == 0L

    }


}