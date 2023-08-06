package com.taxapprf.taxapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.ActivityMainBinding
import com.taxapprf.taxapp.usersdata.Settings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main), Loading {
    private val binding by viewBinding(ActivityMainBinding::bind)
    private val viewModel by viewModels<MainViewModel>()
    private val drawer by lazy { binding.drawerLayout }
    private val mAppBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.rates_today,
                R.id.taxes,
                R.id.account_change,
                R.id.currency_converter,
            ),
            drawer
        )
    }
    private val navController by lazy {
        findNavController(this, R.id.nav_host_fragment_content_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.isSignIn) {
            navController.setGraph(R.navigation.mobile_navigation)
            setSupportActionBar(binding.appBarMain.toolbar)
            setupActionBarWithNavController(
                this@MainActivity,
                navController,
                mAppBarConfiguration
            )
            setupWithNavController(binding.navView, navController)
            prepDrawer()

        }
    }

    private fun prepDrawer() {
        val header = binding.navView.getHeaderView(0)
        header.setOnClickListener {
            navController.navigate(R.id.account_change)
            binding.navView.visibility = View.GONE
        }

        val userAccount = header.findViewById<TextView>(R.id.textNavHeaderUserAccount)
        val settings: SharedPreferences =
            this.getSharedPreferences(Settings.SETTINGSFILE.name, Context.MODE_PRIVATE)
        userAccount.text = settings.getString(Settings.ACCOUNT.name, "")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment_content_main)
        return (navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp())
    }

    override fun onLoadingStart() {
        binding.appBarMain.content.loading.isVisible = true
    }

    override fun onLoadingStop() {
        binding.appBarMain.content.loading.isVisible = false
    }

    override fun onLoadingError(stringResId: Int) {
        onLoadingStop()
    }

    override fun onLoadingSuccess() {
        onLoadingStop()
    }
}