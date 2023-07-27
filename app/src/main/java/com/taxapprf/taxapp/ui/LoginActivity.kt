package com.taxapprf.taxapp.ui

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    val binding by viewBinding(ActivityLoginBinding::bind)
    val viewModel by viewModels<MainViewModel>()

    fun onLoadingStart() {
        binding.loading.isVisible = true
    }

    fun onLoadingStop() {
        binding.loading.isVisible = false
    }

    fun onLoadingError(stringResId: Int) {
        onLoadingStop()
    }

    fun onLoadingSuccess() {
        onLoadingStop()
    }
}