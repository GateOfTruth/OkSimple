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
import com.gateoftruth.sample.databinding.ActivityMultipleDownloadBinding
import okhttp3.Call
import java.io.File
import java.util.*

class MultipleDownloadActivity : AppCompatActivity() {
    val urlPositionMap = hashMapOf<String, Int>()
    val beanList = mutableListOf<DownloadBean>()

    /**
     * 存在问题，先不调试
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityMultipleDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerMultipleDownload.layoutManager = LinearLayoutManager(this)
        val adapter = DownloadAdapter(this)
        val urlList = listOf("download urls")
        val fileNameList = listOf("aps")
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

        binding.btnDeleteAll.setOnClickListener {
            OkSimple.cancelAll()
            val file = File(path)
            if (file.isDirectory) {
                val fileList = file.listFiles()
                fileList?.forEach {
                    it.delete()
                }
            }
        }

        binding.recyclerMultipleDownload.adapter = adapter
        (binding.recyclerMultipleDownload.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }


    override fun onDestroy() {
        super.onDestroy()
        OkSimple.cancelAll()
    }
}
