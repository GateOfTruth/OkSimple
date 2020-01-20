package com.alen.oksimpleutil

interface ImageProgress {
    fun downloadProgress(total: Long, current: Long)
}