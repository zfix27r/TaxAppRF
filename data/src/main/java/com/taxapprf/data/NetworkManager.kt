package com.taxapprf.data

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import javax.inject.Inject


class NetworkManager @Inject constructor(
    private val connectivityManager: ConnectivityManager,
) {
    var isConnection = false

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    fun observeConnection(networkCallback: ConnectivityManager.NetworkCallback) {
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
}