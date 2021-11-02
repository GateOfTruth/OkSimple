package com.gateoftruth.oklibrary

abstract class StringCallBack : DataCallBack<String>() {
    override fun stringToData(string: String): String {
        return string
    }

}