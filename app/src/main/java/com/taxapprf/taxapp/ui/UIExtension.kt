package com.taxapprf.taxapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.ui.activity.ActivityStateLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import kotlin.math.floor

private val _state = ActivityStateLiveData()
val state: LiveData<BaseState> = _state

fun <T> Flow<T>.showLoading() =
    this
        .onStart { _state.loading() }
        .catch { _state.error(it) }
        .onEach { _state.success() }

fun <T> Flow<T>.makeHot(coroutineScope: CoroutineScope, initialValue: T? = null) =
    this
        .flowOn(Dispatchers.IO)
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000L),
            initialValue
        )

fun CoordinatorLayout.showSnackBar(msg: Int) {
    val snack = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT)
    snack.anchorView = this.findViewById(R.id.fab)
    snack.show()
}

fun String.isEmailIncorrect(): Boolean {
    val p = Pattern.compile("\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*\\.\\w{2,4}")
    return !p.matcher(this).matches()
}

fun String.isErrorPasswordRange() = length < 8 || length > 16

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.checkStoragePermission(): Boolean {
    val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    return if (ContextCompat.checkSelfPermission(this, permission)
        == PackageManager.PERMISSION_GRANTED
    ) true
    else {
        ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        false
    }
}

fun Double.round() = floor(this * 100.0) / 100.0