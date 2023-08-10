package com.taxapprf.taxapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.floor

fun View.showSnackBar(msg: Int) =
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()

fun String.isEmailPattern(): Boolean {
    val p = Pattern.compile("\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*\\.\\w{2,4}")
    return p.matcher(this).matches()
}

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

fun Date.format(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(this)
}

fun Double.format() = floor(this * 100.0) / 100.0