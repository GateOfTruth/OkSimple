package com.gateoftruth.oklibrary

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

class ProgressResponseBody(
    private val url: String,
    private val rawResponse: ResponseBody,
    private val baseProgressListener: BaseProgressListener
) : ResponseBody() {
    private val source: BufferedSource? = null
    override fun contentLength(): Long {
        return rawResponse.contentLength()
    }

    override fun contentType(): MediaType? {
        return rawResponse.contentType()
    }

    override fun source(): BufferedSource {
        return source ?: getSource(rawResponse.source()).buffer()
    }


    private fun getSource(source: Source): Source {
        return object : ForwardingSource(source) {
            var downloadByte = 0L
            override fun read(sink: Buffer, byteCount: Long): Long {
                val byteRead = super.read(sink, byteCount)
                if (byteRead != -1L) {
                    downloadByte += byteRead
                }
                baseProgressListener.downloadProgress(
                    url,
                    rawResponse.contentLength(),
                    downloadByte
                )
                return byteRead
            }
        }
    }


}

