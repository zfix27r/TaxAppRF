package com.taxapprf.taxapp.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.textfield.TextInputEditText
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.ui.activity.MainActivity
import com.taxapprf.taxapp.ui.activity.MainViewModel

interface IBaseFragment {
    val mainActivity: MainActivity
    val mainViewModel: MainViewModel
    val fragment: Fragment
    var baseViewModel: BaseViewModel
    var actionMode: ActionMode?

    fun BaseViewModel.attach() {
        baseViewModel = this
        baseViewModel.observeState()
        fragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_PAUSE) {
                    hideActionMode()
                    fragment.lifecycle.removeObserver(this)
                }
            }
        })
    }

    private fun BaseViewModel.observeState() {
        state.observe(fragment.viewLifecycleOwner) {
            when (it) {
                is Loading -> onLoading()
                is Error -> onError(it.t)
                is Success -> onSuccess()
                is SignOut -> mainActivity.onSignOut()
                is SuccessShare -> onSuccessShare(it.uri)
                is SuccessImport -> onSuccessImport(it.uri)
                is SuccessDelete -> onSuccessDelete()
            }
        }
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

    fun onSuccessShare(uri: Uri) {
        mainActivity.onLoadingSuccess()

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "vnd.android.cursor.dir/email"
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, fragment.getString(R.string.share_message_name))
        fragment.startActivity(
            Intent.createChooser(
                emailIntent,
                fragment.getString(R.string.share_message_description)
            )
        )
    }

    fun onSuccessImport(uri: Uri) {
        mainActivity.onLoadingSuccess()
    }

    fun onSuccessDelete() {
        mainActivity.onLoadingSuccess()
    }

    fun showActionMode(callback: () -> BaseActionModeCallback) {
        actionMode = mainActivity.startSupportActionMode(callback.invoke())
    }

    fun hideActionMode() {
        actionMode?.let {
            it.finish()
            actionMode = null
        }
    }

    fun Int?.updateEditError(edit: TextInputEditText): Boolean {
        this?.let {
            edit.error = fragment.getString(it)
            return false
        } ?: run {
            edit.error = null
            return true
        }
    }
}