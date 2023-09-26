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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import kotlin.math.floor

const val PATTERN_DATE = "dd/MM/uuuu"

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

val formatter = DateTimeFormatter.ofPattern(PATTERN_DATE)

fun Long.formatDate(): String =
    LocalDate.ofEpochDay(this).format(formatter)

fun Double.round() = floor(this * 100.0) / 100.0

fun Activity.share(uri: Uri) {
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.type = "vnd.android.cursor.dir/email"
    emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Расчёт налога от TaxApp")
    startActivity(Intent.createChooser(emailIntent, "Send email..."))
}

fun Int.getTransactionName() = when (this) {
    TransactionType.COMMISSION.k -> R.string.transaction_type_commission
    TransactionType.FUNDING_WITHDRAWAL.k -> R.string.transaction_type_funding_withdrawal
    else -> R.string.transaction_type_trade
}

fun Activity.getTransactionType(typeName: String) = when (typeName) {
    getString(R.string.transaction_type_commission) -> TransactionType.COMMISSION.k
    getString(R.string.transaction_type_funding_withdrawal) -> TransactionType.FUNDING_WITHDRAWAL.k
    else -> TransactionType.TRADE.k
}