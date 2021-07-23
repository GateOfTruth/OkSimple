package com.gateoftruth.oklibrary

class NormalSynchronizeBean<T>:BaseSynchronizeBean<T>() {

    override fun responseToData(): T? {
        return null
    }
}