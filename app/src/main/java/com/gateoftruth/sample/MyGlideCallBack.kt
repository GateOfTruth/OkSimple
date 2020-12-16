package com.gateoftruth.sample

import com.gateoftruth.oklibrary.GlideCallBack
import com.gateoftruth.oklibrary.OkSimple

class MyGlideCallBack : GlideCallBack() {
    val urlProgressListener = hashMapOf<String, ImageProgress>()

    companion object {
        val instance = MyGlideCallBack()
    }

    override fun downloadProgress(url: String, total: Long, current: Long) {
        OkSimple.mainHandler.post {
            downloadProgressOnMainThread(url, total, current)
        }
    }

    override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {
        urlProgressListener[url]?.downloadProgress(total, current)
        if (total == current) {
            urlProgressListener.remove(url)
        }
    }
}