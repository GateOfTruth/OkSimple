package com.alen.oksimpleutil

import android.content.Context
import com.alen.oklibrary.OkSimple
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

@GlideModule
class GlideModule :AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.replace(GlideUrl::class.java,InputStream::class.java,OkHttpUrlLoader.Factory(OkSimple.getGlideClient(MyGlideCallBack.instance)))
    }
}