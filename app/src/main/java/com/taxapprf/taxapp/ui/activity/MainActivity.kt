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
import com.taxapprf.domain.main.account.AccountModel
import com.taxapprf.domain.main.user.UserWithAccountsModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.ActivityMainBinding
import com.taxapprf.taxapp.ui.MainToolbar
import com.taxapprf.taxapp.ui.account.add.AccountAddFragment
import com.taxapprf.taxapp.ui.drawer.Drawer
import com.taxapprf.taxapp.ui.drawer.DrawerCallback
import com.taxapprf.taxapp.ui.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main),
    AccountAddFragment.AccountAddDialogListener {
    private val binding by viewBinding(ActivityMainBinding::bind)
    private val viewModel by viewModels<MainViewModel>()

    private val mAppBarConfiguration by lazy {
        AppBarConfiguration(topLevelDestinations, binding.drawerLayout)
    }

    private val navController by lazy {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navHost.navController
    }

    val drawer by lazy {
        Drawer(
            binding.drawerLayout,
            binding.navView,
            navController,
            drawerCallback
        )
    }
    val toolbar by lazy { MainToolbar(binding.appBarMain.toolbar) }
    val fab by lazy { binding.appBarMain.fab }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.appBarMain.toolbar)
        setupActionBarWithNavController(
            this@MainActivity, navController, mAppBarConfiguration
        )
        setupWithNavController(binding.navView, navController)

        navController.setGraph(R.navigation.mobile_navigation)

        observeUser()
        startSync()
    }

    override fun onStart() {
        super.onStart()
        viewModel.defaultAccountName = getString(R.string.default_account_name)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_reports, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment_content_main)
        return (navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp())
    }

    private fun onAccountLoaded(userWithAccountsModel: UserWithAccountsModel?) {
        userWithAccountsModel?.let {
            userWithAccountsModel.activeAccount?.let {
                viewModel.accountId = it.id
                navController.observeCurrentBackStack()
            }
        }
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

    private fun Int.showErrorInShackBar() {
        binding.appBarMain.root.showSnackBar(this)
    }

    fun onLoadingSuccess() {
        onLoadingStop()
    }

    private val topLevelDestinations = setOf(
        R.id.sign,
        R.id.currency_rate,
        R.id.reports,
        R.id.currency_converter,
    )

    private val fabVisibleDestinations = setOf(
        R.id.reports,
        R.id.transactions,
    )

    private fun startSync() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.syncAll().collectLatest {
                    viewModel.updateUserWithAccounts()
                }
            }
        }
    }

    private fun observeUser() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userWithAccounts.collectLatest { userWithAccounts ->
                    drawer.update(
                        userWithAccountsModel = userWithAccounts,
                        defaultUserName = getString(R.string.default_account_name)
                    )
                    onAccountLoaded(userWithAccounts)
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
            override fun signOut() {
                viewModel.signOut()
            }

            override fun switchAccount(accountModel: AccountModel) {
                viewModel.switchAccount(accountModel.name)
            }
        }

    override fun onAccountAddPositiveClick(dialog: AccountAddFragment) {
        dialog.dismiss()
        viewModel.switchAccount(dialog.getAccountName())
    }

    override fun onAccountAddNegativeClick(dialog: AccountAddFragment) {
        dialog.dismiss()
    }
}