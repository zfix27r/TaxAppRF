package com.taxapprf.taxapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taxapprf.domain.user.IsSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isSignInUseCase: IsSignInUseCase,
) : ViewModel() {
    val isSignIn
        get() = isSignInUseCase.execute()

    private var _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private var _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail
}