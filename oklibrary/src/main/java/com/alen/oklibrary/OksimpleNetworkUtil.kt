package com.alen.oklibrary

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest

object OksimpleNetworkUtil {
    var connectivityManager: ConnectivityManager? = null
    fun init(application: Application) {
        connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val manager = connectivityManager
            manager?.registerNetworkCallback(NetworkRequest.Builder().build(),
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        OkSimple.networkAvailable = true
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        OkSimple.networkAvailable = false
                    }
                })

    }

    fun isNetworkAvailable(): Boolean {
        val manager= connectivityManager
        return  if (manager==null) true else OkSimple.networkAvailable
    }


}