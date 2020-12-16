package com.gateoftruth.sample

import com.gateoftruth.oklibrary.RequestUntilSuccess
import okhttp3.Request

class ChangeHostStrategy : RequestUntilSuccess() {

    /**
     * count>0表示请求失败，失败之后动态切换域名或者重写header都是可以的
     */
    override fun getRequestBuilder(builder: Request.Builder): Request.Builder {
        return if (count > 0) builder.url("https://google.com") else builder
    }
}
