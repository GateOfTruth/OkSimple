package com.gateoftruth.sample

import com.gateoftruth.oklibrary.BaseSynchronizeBean
import com.google.gson.Gson
import java.lang.reflect.ParameterizedType

class GsonSyncBeanBase<T>(private val cls:Class<T>):BaseSynchronizeBean<T>() {

    private val gson = Gson()

    override fun responseToData(): T? {
        return gson.fromJson(response?.body!!.string(), cls)
    }

}