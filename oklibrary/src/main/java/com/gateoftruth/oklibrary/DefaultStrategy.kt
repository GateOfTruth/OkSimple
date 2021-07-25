package com.gateoftruth.oklibrary

import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 默认的策略，无论成功失败和返回结果，都只请求一次
 */
open class DefaultStrategy : RequestStrategy() {
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
        return false
    }

    override fun getRequestBuilder(builder: Request.Builder): Request.Builder {
        return builder
    }

    override fun requestDelay(): Long {
        return 0
    }

    override fun maxRequestTimes(): Long {
        return 0
    }

    override fun callBackStart(): Boolean {
        return true
    }
}