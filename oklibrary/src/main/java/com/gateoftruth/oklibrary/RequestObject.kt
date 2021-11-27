package com.gateoftruth.oklibrary

data class RequestObject(val tag: String, val contentString: String, val contentLength: Long) {

    override fun equals(other: Any?): Boolean {
        if (other is RequestObject) {
            return tag == other.tag && contentString == other.contentString && contentLength == other.contentLength
        } else {
            return super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + contentString.hashCode()
        result = 31 * result + contentLength.hashCode()
        return result
    }

    override fun toString(): String {
        return "request tag:$tag;contentString:$contentString;contentLength:contentLength:$contentLength"
    }


}