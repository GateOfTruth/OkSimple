package com.gateoftruth.oklibrary

data class RequestObject(val tag:String,val contentLength:Long) {
    
    override fun equals(other: Any?): Boolean {
        if (other is RequestObject){
            return tag==other.tag&&contentLength==other.contentLength
        }else{
            return super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + contentLength.hashCode()
        return result
    }
}