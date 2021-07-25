package com.gateoftruth.oklibrary

import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

abstract class RequestStrategy {
    open var strategyResultCallBack: ResultCallBack? = null

    open var strategyCall: Call? = null

    open var count = 0L

    /**
     * 返回response时候是否继续请求
     */
    abstract fun requestAgainOnResponse(call: Call, response: Response): Boolean

    /**
     * 是否调用ResultCallBack的Response方法
     */
    abstract fun callBackResponse(call: Call, response: Response): Boolean

    /**
     * 是否调用ResultCallBack的Failure方法
     */
    abstract fun callBackFailure(call: Call, e: IOException): Boolean

    /**
     * 失败时候的是否请求
     */
    abstract fun requestAgainOnFailure(call: Call, e: IOException): Boolean

    /**
     *重写url或者header用
     */
    abstract fun getRequestBuilder(builder: Request.Builder): Request.Builder

    /**
     * 延迟时间毫秒数
     */
    abstract fun requestDelay(): Long

    /**
     * 最大请求次数
     */
    abstract fun maxRequestTimes(): Long

    /**
     * 是否调用ResultCallBack里的start方法
     */
    abstract fun callBackStart(): Boolean


}
