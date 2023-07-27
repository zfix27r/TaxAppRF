package com.taxapprf.taxapp.ui

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.SavedStateHandle
import com.google.android.material.snackbar.Snackbar
import com.taxapprf.domain.FirebaseRequestModel
import java.util.regex.Pattern

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

const val YEAR = "year"
const val ACCOUNT = "account"
const val KEY = "key"

fun SavedStateHandle.toFirebaseModel() =
    FirebaseRequestModel(
        account = get<String>(YEAR) ?: "",
        year = get<String>(ACCOUNT) ?: "",
        key = get<String>(KEY) ?: "",
    )