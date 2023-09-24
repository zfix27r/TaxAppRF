package com.taxapprf.taxapp.ui.activity

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.data.error.DataErrorCBR
import com.taxapprf.data.error.DataErrorConnection
import com.taxapprf.data.error.DataErrorExcel
import com.taxapprf.data.error.DataErrorExternal
import com.taxapprf.data.error.DataErrorInternal
import com.taxapprf.data.error.DataErrorUser
import com.taxapprf.data.error.DataErrorUserEmailAlreadyUse
import com.taxapprf.data.error.DataErrorUserWrongPassword
import com.taxapprf.domain.user.AccountModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.ActivityMainBinding
import com.taxapprf.taxapp.ui.Error
import com.taxapprf.taxapp.ui.Loading
import com.taxapprf.taxapp.ui.drawer.Drawer
import com.taxapprf.taxapp.ui.MainToolbar
import com.taxapprf.taxapp.ui.SignOut
import com.taxapprf.taxapp.ui.Success
import com.taxapprf.taxapp.ui.drawer.DrawerCallback
import com.taxapprf.taxapp.ui.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding(ActivityMainBinding::bind)
    private val viewModel by viewModels<MainViewModel>()

    private val mAppBarConfiguration by lazy {
        AppBarConfiguration(topLevelDestinations, binding.drawerLayout)
    }

    val navController by lazy {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navHost.navController
    }

    val drawer by lazy { Drawer(binding.drawerLayout, binding.navView, drawerCallback) }
    val toolbar by lazy { MainToolbar(binding.appBarMain.toolbar) }
    val fab by lazy { binding.appBarMain.fab }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.appBarMain.toolbar)
        setupActionBarWithNavController(
            this@MainActivity, navController, mAppBarConfiguration
        )
        setupWithNavController(binding.navView, navController)

        viewModel.observeState()
        viewModel.observeUser()
        navController.observeCurrentBackStack()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /*        if (viewModel.isSignIn) menuInflater.inflate(R.menu.reports_toolbar, menu)
                else menuInflater.inflate(R.menu.main_toolbar, menu)*/
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment_content_main)
        return (navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp())
    }

    fun onLoadingStart() {
        with(binding.appBarMain.content) {
            loadingErrorGroup.isVisible = false
            loading.isVisible = true
        }
    }

    private fun onLoadingStop() {
        binding.appBarMain.content.loading.isVisible = false
    }

    fun onLoadingError(t: Throwable) {
        onLoadingStop()
        when (t) {
            is SocketTimeoutException -> R.string.data_error_socket_timeout.showErrorInShackBar()
            is DataErrorUser -> R.string.auth_error.showErrorInShackBar()
            is DataErrorInternal -> R.string.data_error_internal.showErrorInShackBar()
            is DataErrorExternal -> R.string.data_external_error.showErrorInShackBar()
            is DataErrorExcel -> R.string.data_error_excel.showErrorInShackBar()
            is DataErrorCBR -> R.string.data_error_cbr.showErrorInShackBar()
            is DataErrorConnection -> R.string.data_error_connection.showErrorInShackBar()
            is DataErrorUserWrongPassword -> R.string.error_sign_in.showErrorInShackBar()
            is DataErrorUserEmailAlreadyUse -> R.string.sign_up_error_email_already_use.showErrorInShackBar()
            else -> throw t//R.string.data_error.showErrorInShackBar()
        }
    }

    private fun Int.showError() {
        binding.appBarMain.content.loadingErrorGroup.isVisible = true
        binding.appBarMain.content.loadingErrorMessage.text = getString(this)
    }

    private fun Int.showErrorInShackBar() {
        binding.appBarMain.root.showSnackBar(this)
    }

    fun onLoadingSuccess() {
        onLoadingStop()
    }

    private fun onSignOut() {
        viewModel.signOut()
    }

    private val topLevelDestinations = setOf(
        R.id.sign,
        R.id.currency_rates_today,
        R.id.reports,
        R.id.currency_converter,
    )

    private val fabVisibleDestinations = setOf(
        R.id.reports,
        R.id.transactions,
    )

    private fun MainViewModel.observeState() {
        state.observe(this@MainActivity) {
            when (it) {
                is Loading -> onLoadingStart()
                is Error -> onLoadingError(it.t)
                is Success -> onLoadingSuccess()
                is SignOut -> onSignOut()
                else -> {}
            }
        }
    }

    private fun MainViewModel.observeUser() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userWithAccounts.collectLatest { userWithAccounts ->
                    drawer.updateUser(
                        userWithAccounts?.user,
                        getString(R.string.default_user_name_name)
                    )
                    drawer.updateActiveAccount(userWithAccounts?.activeAccount)
                    drawer.updateOtherAccounts(userWithAccounts?.otherAccounts)
                }
            }
        }
    }

    private fun NavController.observeCurrentBackStack() {
        lifecycle.coroutineScope.launch {
            currentBackStack.collectLatest {
                fabVisibilityManager()
            }
        }
    }

    private fun fabVisibilityManager() {
        val currentDestination = navController.currentBackStack.value.last().destination.id
        if (fabVisibleDestinations.contains(currentDestination)) binding.appBarMain.fab.show()
        else binding.appBarMain.fab.hide()
    }

    private val drawerCallback =
        object : DrawerCallback {
            override fun navToReports() {
                navController.navigate(R.id.action_global_reports)
            }

            override fun navToCurrencyConverter() {
                navController.navigate(R.id.action_global_currency_converter)
            }

            override fun navToCurrencyRatesToday() {
                navController.navigate(R.id.action_global_currency_rates_today)
            }

            override fun navToSign() {
                navController.navigate(R.id.action_global_sign)
            }

            override fun navToSignOut() {
                viewModel.signOut()
            }

            override fun navToAccountAdd() {
                if (navController.currentDestination?.id != R.id.account_add)
                    navController.navigate(R.id.action_global_account_add)
            }

            override fun switchAccount(accountModel: AccountModel) {
                viewModel.switchAccount(accountModel)
            }
        }
}