package com.gateoftruth.sample

import com.gateoftruth.oklibrary.RequestUntilSuccess
import okhttp3.Request
import java.net.URL

class ChangeHostStrategy : RequestUntilSuccess() {

    /**
     * 请求失败后更换host的demo
     * count>0表示请求失败，失败之后动态切换域名或者重写header都是可以的
     * 注意，请自行解析url并更换host，requestbuilder拿到的是拼接过参数的url，这里不会再次拼接全局参数等各种参数，但是header会保留
     */
    override fun getRequestBuilder(builder: Request.Builder): Request.Builder {
        val oldRequest = builder.build()
        val oldUrl = URL(oldRequest.url.toString())
        val newUrl = URL(oldUrl.protocol, "google.com", oldUrl.file)
        return if (count > 0) oldRequest.newBuilder().url(newUrl) else builder
    }
}
