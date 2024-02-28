package com.gateoftruth.sample

import com.gateoftruth.oklibrary.DataCallBack
import com.google.gson.Gson
import java.lang.reflect.ParameterizedType

/**
 * 仅供参考，实际使用中需要优化，比如可以考虑把gson设置为单例等等
 */
abstract class GsonCallBack<E> : DataCallBack<E>() {
    private val gson = Gson()
    override fun stringToData(string: String): E {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        return gson.fromJson(string, type)
    }

}