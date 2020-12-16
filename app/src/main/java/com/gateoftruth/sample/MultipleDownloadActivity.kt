package com.gateoftruth.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.gateoftruth.oklibrary.FileResultCallBack
import com.gateoftruth.oklibrary.OkSimple
import kotlinx.android.synthetic.main.activity_multiple_download.*
import okhttp3.Call
import java.io.File
import java.util.*

class MultipleDownloadActivity : AppCompatActivity() {
    val urlPositionMap = hashMapOf<String, Int>()
    val beanList = mutableListOf<DownloadBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_download)
        recycler_multiple_download.layoutManager = LinearLayoutManager(this)
        val adapter = DownloadAdapter(this)
        val urlList = listOf(
            "http://dl-ks.coolapkmarket.com/down/apk_file/2019/1029/Coolapk-9.6.3-1910291-coolapk-app-release.apk?t=1575368046&k=d5389a67fabe01c41ba81aa3cc293f2f",
            "http://imtt.dd.qq.com/16891/apk/A013F5C61B2708769B0D708AA7A25E57.apk?fsname=com.tencent.mm_7.0.9_1560.apk&csr=db5e",
            "http://imtt.dd.qq.com/16891/apk/B2081E1D5C4EBD7E0097EFD2F1277347.apk?fsname=com.netease.cloudmusic_6.4.8_163.apk&csr=db5e",
            "http://imtt.dd.qq.com/16891/apk/BA601A280323494CADAE2416B8A81AC4.apk?fsname=com.tencent.mobileqq_8.1.8_1276.apk&csr=db5e",
            "http://dl.hdslb.com/mobile/latest/iBiliPlayer-bilibili140.apk",
            "http://download.alicdn.com/wireless/taobao4android/latest/taobao4android_1568708051313.apk",
            "http://qn-apk.wdjcdn.com/4/a0/737eea1d6d8b4f63167ed561d7623a04.apk",
            "http://tfs.alipayobjects.com/L1/71/100/and/alipay_2088131876115982_246.apk",
            "http://imtt.dd.qq.com/16891/apk/1C3422FF217BF22EF9106021E1E37E5F.apk?fsname=com.baidu.netdisk_10.0.114_1038.apk&csr=db5e",
            "http://imtt.dd.qq.com/16891/apk/FD72A6BAFC870B7483DA86EE6CD63B40.apk?fsname=com.snda.wifilocating_4.5.29_191130.apk&csr=db5e",
            "http://dl-cdn.coolapkmarket.com/down/apk_upload/2019/1011/Greenify.v4.7.5-7232-o_1dmtieem7u1b5og1spk1coj1j8mq-uid-416516.apk?_upt=38d6aa7b1575368371",
            "http://imtt.dd.qq.com/16891/apk/501310ECBE19300367FE66CE655187F1.apk?fsname=com.jingdong.app.mall_8.4.0_70797.apk&csr=db5e"
        )
        val fileNameList = listOf(
            "kuan.apk",
            "wechat.apk",
            "cloudmusic.apk",
            "qq.apk",
            "bilibili.apk",
            "taobao.apk",
            "weibo.apk",
            "alipay.apk",
            "netdisk.apk",
            "wifikey.apk",
            "Greenify.apk",
            "jd.apk"
        )
        urlList.forEachIndexed { index, s ->
            val bean = DownloadBean(0, s, fileNameList[index])
            beanList.add(bean)
            urlPositionMap[s] = index
        }
        val path = getExternalFilesDir("download")?.absolutePath ?: ""
        adapter.downloadBeanList.addAll(beanList)
        val callBack = object : FileResultCallBack() {
            override fun finish(file: File) {
                Toast.makeText(
                    this@MultipleDownloadActivity,
                    "${file.name}下载完成",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun failure(call: Call, e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@MultipleDownloadActivity,
                    "下载失败",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {
                val percent = current / total.toDouble()
                val realPercent = (percent * 100).toInt()
                val adapterPosition = urlPositionMap[url]
                if (adapterPosition != null) {
                    Log.e("download", "${url}\t\t${realPercent}")
                    val bean = beanList[adapterPosition]
                    bean.progress = realPercent
                    adapter.notifyItemChanged(adapterPosition)
                }
            }

        }
        adapter.itemClickInterface = object : DownloadAdapter.ItemClickInterface {
            override fun itemClick(position: Int, view: View) {
                when (view.id) {
                    R.id.btn_item_start_download -> {
                        OkSimple.downloadFile(
                            beanList[position].url,
                            beanList[position].fileName,
                            path
                        )
                            .execute(callBack)
                    }

                    R.id.btn_item_pause_download -> {
                        OkSimple.cancelCall(beanList[position].url)
                    }

                    R.id.btn_item_delete_file -> {
                        OkSimple.cancelCall(beanList[position].url)
                        val file = File(path, beanList[position].fileName)
                        file.delete()
                    }
                }
            }
        }

        btn_delete_all.setOnClickListener {
            OkSimple.cancelAll()
            val file = File(path)
            if (file.isDirectory) {
                val fileList = file.listFiles()
                fileList?.forEach {
                    it.delete()
                }
            }
        }

        recycler_multiple_download.adapter = adapter
        (recycler_multiple_download.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }


    override fun onDestroy() {
        super.onDestroy()
        OkSimple.cancelAll()
    }
}
