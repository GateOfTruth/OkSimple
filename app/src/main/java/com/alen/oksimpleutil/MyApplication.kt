package com.alen.oksimpleutil

import android.app.Application
import com.alen.oklibrary.OkSimple
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        /*
        init your own okHttpClient if necessary
         */
        val logger=LoggingInterceptor.Builder().loggable(true).setLevel(Level.BASIC).request("REQUEST").response("RESPONSE").build()
        OkSimple.okHttpClient=OkHttpClient.Builder().addInterceptor(logger)
            .connectTimeout(100,TimeUnit.SECONDS)
            .writeTimeout(100,TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS)
            .cache(Cache(cacheDir,1024*1024*10))
            .build()
        OkSimple.application=this
        /*
        replace by your global params and global header
         */
        /*OkSimple.addGlobalParams("testGlobalParams","1")
        OkSimple.addGlobalHeader("testGlobalHeader","2")*/
    }
}