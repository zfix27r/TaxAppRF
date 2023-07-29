package com.taxapprf.taxapp.ui

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(R.layout.activity_login), Loading {
    val binding by viewBinding(ActivityLoginBinding::bind)
    val viewModel by viewModels<MainViewModel>()

    // TODO при переходе из sign в accountNew не очищается бэкстэк
    override fun onLoadingStart() {
        binding.loading.isVisible = true
    }

    override fun onLoadingStop() {
        binding.loading.isVisible = false
    }

    override fun onLoadingError(stringResId: Int) {
        onLoadingStop()
    }

    override fun onLoadingSuccess() {
        onLoadingStop()
    }
}