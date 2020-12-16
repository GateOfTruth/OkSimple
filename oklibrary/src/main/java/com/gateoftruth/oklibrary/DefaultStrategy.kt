package com.gateoftruth.oklibrary

import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 默认的策略，无论成功失败和返回结果，都只请求一次
 */
open class DefaultStrategy : RequestStrategy() {
    override fun doRequestWhenOnResponse(call: Call, response: Response): Boolean {
        return false
    }

    override fun doResultCallBackResponse(call: Call, response: Response): Boolean {
        return true
    }

    override fun doResultCallBackFailure(call: Call, e: IOException): Boolean {
        return true
    }

    override fun doRequestWhenOnFailure(call: Call, e: IOException): Boolean {
        return false
    }

    override fun getRequestBuilder(builder: Request.Builder): Request.Builder {
        return builder
    }

    override fun delay(): Long {
        return 0
    }

    override fun maxNumberOfTimes(): Long {
        return 0
    }

    override fun callStartFunction(): Boolean {
        return true
    }
}