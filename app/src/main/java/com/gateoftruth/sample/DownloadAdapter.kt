package com.gateoftruth.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class DownloadAdapter(context: Context) : RecyclerView.Adapter<DownloadAdapter.MyHolder>() {
    private val layoutInflater = LayoutInflater.from(context)
    val downloadBeanList = mutableListOf<DownloadBean>()
    lateinit var itemClickInterface: ItemClickInterface

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(layoutInflater.inflate(R.layout.item_multiple_download, parent, false))
    }

    override fun getItemCount(): Int {
        return downloadBeanList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val pauseButton = holder.itemView.findViewById<Button>(R.id.btn_item_pause_download)
        val startButton = holder.itemView.findViewById<Button>(R.id.btn_item_start_download)
        val deleteButton = holder.itemView.findViewById<Button>(R.id.btn_item_delete_file)
        val fileNameTextView = holder.itemView.findViewById<TextView>(R.id.tv_item_file_name)
        val progressTextView = holder.itemView.findViewById<TextView>(R.id.tv_item_progress)
        val progress = holder.itemView.findViewById<ProgressBar>(R.id.progress_item)
        val clickListener = View.OnClickListener {
            itemClickInterface.itemClick(holder.adapterPosition, it)
        }
        pauseButton.setOnClickListener(clickListener)
        startButton.setOnClickListener(clickListener)
        deleteButton.setOnClickListener(clickListener)
        val downloadBean = downloadBeanList[position]
        fileNameTextView.text = downloadBean.fileName
        progress.progress = downloadBean.progress
        progressTextView.text = "${downloadBean.progress}%"
    }


    interface ItemClickInterface {
        fun itemClick(position: Int, view: View)
    }
}