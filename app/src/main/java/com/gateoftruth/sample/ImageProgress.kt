package com.gateoftruth.sample

interface ImageProgress {
    fun downloadProgress(total: Long, current: Long)
}