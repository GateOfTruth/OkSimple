package com.alen.oksimpleutil

import com.alen.oklibrary.DataCallBack
import com.google.gson.Gson
import java.lang.reflect.ParameterizedType

abstract class GsonCallBack<E> :DataCallBack<E>() {
    private val gson=Gson()
    override fun stringToData(string: String): E {
        val type=(javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        return gson.fromJson(string,type)
    }

}