package com.alen.oksimpleutil

import com.alen.oklibrary.GlideCallBack
import com.alen.oklibrary.OkSimple

class MyGlideCallBack: GlideCallBack() {
    val urlProgressListener= hashMapOf<String,ImageProgress>()
    companion object{
        val instance=MyGlideCallBack()
    }

    override fun downloadProgress(url: String, total: Long, current: Long) {
        OkSimple.mainHandler.post {
            downloadProgressOnMainThread(url, total, current)
        }
    }

    override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {
        urlProgressListener[url]?.downloadProgress( total, current)
        if (total==current){
            urlProgressListener.remove(url)
        }
    }
}