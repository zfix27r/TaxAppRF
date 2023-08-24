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
import com.google.android.material.snackbar.Snackbar
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.floor

fun CoordinatorLayout.showSnackBar(msg: Int) =
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()

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

fun Date.format(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(this)
}

fun Double.format() = floor(this * 100.0) / 100.0

fun Activity.share(uri: Uri) {
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.type = "vnd.android.cursor.dir/email"
    emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Расчёт налога от TaxApp")
    startActivity(Intent.createChooser(emailIntent, "Send email..."))
}

fun String.getTransactionName() = when (this) {
    TransactionType.COMMISSION.name -> R.string.transaction_type_commission
    TransactionType.FUNDING_WITHDRAWAL.name -> R.string.transaction_type_funding_withdrawal
    else -> R.string.transaction_type_trade
}

fun Activity.getTransactionType(typeName: String) = when (typeName) {
    getString(R.string.transaction_type_commission) -> TransactionType.COMMISSION.name
    getString(R.string.transaction_type_funding_withdrawal) -> TransactionType.FUNDING_WITHDRAWAL.name
    else -> TransactionType.TRADE.name
}