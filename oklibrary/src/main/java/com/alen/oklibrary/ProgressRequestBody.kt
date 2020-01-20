package com.alen.oklibrary

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*

class ProgressRequestBody(private val fileName:String,private val requestBody: RequestBody,private val baseProgressListener: BaseProgressListener):RequestBody() {
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    override fun writeTo(sink: BufferedSink) {
        val bufferedSink=getSink(sink).buffer()
        requestBody.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    private fun getSink(sink: Sink):Sink{
        return object :ForwardingSink(sink){
            var uploadByte=0L
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                uploadByte+=byteCount
                baseProgressListener.uploadProgress(fileName,requestBody.contentLength(),uploadByte)
            }
        }
    }
}