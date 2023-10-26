package com.taxapprf.taxapp.ui.extension

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val WRITE_EXTERNAL_STORAGE_MAX_SDK = 29
private const val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
private const val PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

fun Activity.checkStoragePermission() =
    if (Build.VERSION.SDK_INT <= WRITE_EXTERNAL_STORAGE_MAX_SDK) {
        if (
            ContextCompat.checkSelfPermission(this, PERMISSION_WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, PERMISSION_READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) true
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(PERMISSION_READ_EXTERNAL_STORAGE, PERMISSION_WRITE_EXTERNAL_STORAGE),
                1
            )
            false
        }
    } else true
