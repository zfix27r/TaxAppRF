package com.taxapprf.taxapp.ui

interface Loading {
    fun onLoadingStart()

    fun onLoadingStop()

    fun onLoadingError(stringResId: Int)

    fun onLoadingSuccess()
}