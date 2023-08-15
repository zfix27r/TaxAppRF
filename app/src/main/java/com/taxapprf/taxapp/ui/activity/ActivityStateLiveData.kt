package com.taxapprf.taxapp.ui.activity

import androidx.lifecycle.MutableLiveData

class ActivityStateLiveData : MutableLiveData<ActivityBaseState>() {
    fun loading() {
        postValue(Loading)
    }

    fun error(t: Throwable) {
        postValue(Error(t))
    }

    fun success() {
        postValue(Success)
    }

    fun signOut() {
        postValue(SignOut)
    }
}