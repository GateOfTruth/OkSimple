package com.gateoftruth.oklibrary

import android.os.Handler
import android.os.Looper
import android.os.Message

class OksimpleHandler : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
        if (msg.what == OkSimpleConstant.STRATEGY_MESSAGE) {
            val strategy = msg.obj as RequestStrategy
            val max = strategy.maxNumberOfTimes()
            if (max > strategy.count) {
                OkSimple.strategyRequest(strategy)
            }
        }

    }
}