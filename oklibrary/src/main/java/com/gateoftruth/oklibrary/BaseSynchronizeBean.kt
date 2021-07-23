package com.gateoftruth.oklibrary

import okhttp3.Response

abstract class BaseSynchronizeBean<T> {

    var response: Response? = null

    var exception: Exception? = null

    var data: T? = null

    abstract fun responseToData(): T?
}