package com.taxapprf.data

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.taxapprf.data.error.DataErrorConnection
import javax.inject.Inject


class NetworkManager @Inject constructor(
    connectivityManager: ConnectivityManager,
) {
    private var _available = false
    val isConnection
        get() = _available

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            _available = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _available = false
        }
    }

    fun isConnectionOrThrow() = if (isConnection) isConnection else throw DataErrorConnection()

    init {
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
}