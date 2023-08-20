package com.taxapprf.taxapp.ui.activity

import androidx.lifecycle.MutableLiveData
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.Loading
import com.taxapprf.taxapp.ui.SignOut
import com.taxapprf.taxapp.ui.Error
import com.taxapprf.taxapp.ui.Success

class ActivityStateLiveData : MutableLiveData<BaseState>() {
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