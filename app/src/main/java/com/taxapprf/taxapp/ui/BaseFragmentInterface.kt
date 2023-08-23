package com.taxapprf.taxapp.ui

import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.taxapprf.taxapp.ui.activity.MainActivity
import com.taxapprf.taxapp.ui.activity.MainViewModel

interface BaseFragmentInterface {
    val mainActivity: MainActivity
    val mainViewModel: MainViewModel
    val fragment: Fragment
    var baseViewModel: BaseViewModel
    var actionMode: ActionMode?


    fun BaseViewModel.attach() {
        baseViewModel = this
        baseViewModel.observeState()
        mainActivity.binding.appBarMain.content.loadingRetry.setOnClickListener { onLoadingRetry() }
    }

    fun BaseViewModel.attachWithAccount() {
        attach()
        mainViewModel.observeAccount()
    }

    private fun MainViewModel.observeAccount() {
        account.observe(fragment.viewLifecycleOwner) { _account ->
            _account?.let {
                baseViewModel.account = it
                onAuthReady()
            } ?: fragment.findNavController().popBackStack()
        }
    }

    private fun BaseViewModel.observeState() {
        state.observe(fragment.viewLifecycleOwner) {
            when (it) {
                is Loading -> onLoading()
                is Error -> onError(it.t)
                is Success -> onSuccess()
                is SignOut -> mainViewModel.signOut()
                is SuccessShare -> onSuccessShare()
                is SuccessImport -> onSuccessImport()
                is SuccessDelete -> onSuccessDelete()
            }
        }
    }

    fun onAuthReady() {

    }

    fun onLoading() {
        mainActivity.onLoadingStart()
    }

    fun onLoadingRetry() {

    }

    fun onError(t: Throwable) {
        mainActivity.onLoadingError(t)
    }

    fun onSuccess() {
        mainActivity.onLoadingSuccess()
    }

    fun onSuccessShare() {
        mainActivity.onLoadingSuccess()
    }

    fun onSuccessImport() {
        mainActivity.onLoadingSuccess()
    }

    fun onSuccessDelete() {
        mainActivity.onLoadingSuccess()
    }

    fun showActionMode(callback: () -> BaseActionModeCallback) {
        actionMode = mainActivity.startSupportActionMode(callback.invoke())
    }
}