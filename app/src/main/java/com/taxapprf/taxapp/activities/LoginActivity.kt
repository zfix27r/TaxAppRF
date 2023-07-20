package com.taxapprf.taxapp.activities

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.taxapprf.taxapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    val viewModel by viewModels<MainViewModel>()
}