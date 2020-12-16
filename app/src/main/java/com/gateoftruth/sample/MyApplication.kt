package com.gateoftruth.sample

import android.app.Application
import com.gateoftruth.oklibrary.OkSimple
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        /*
        init your own okHttpClient if necessary
         */
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkSimple.okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .connectTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .cache(Cache(cacheDir, 1024 * 1024 * 10))
            .build()
        OkSimple.application = this
        /*
        replace by your global params and global header
         */
        /*OkSimple.addGlobalParams("testGlobalParams","1")
        OkSimple.addGlobalHeader("testGlobalHeader","2")*/
    }
}