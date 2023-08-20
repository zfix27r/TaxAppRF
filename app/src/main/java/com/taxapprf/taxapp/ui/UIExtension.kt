package com.taxapprf.taxapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.taxapprf.data.error.DataErrorExcel
import com.taxapprf.data.error.DataErrorExternal
import com.taxapprf.data.error.DataErrorInternal
import com.taxapprf.data.error.DataErrorUser
import com.taxapprf.data.error.DataErrorUserEmailAlreadyUse
import com.taxapprf.data.error.DataErrorUserWrongPassword
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.ui.error.UIErrorEmailEmpty
import com.taxapprf.taxapp.ui.error.UIErrorEmailIncorrect
import com.taxapprf.taxapp.ui.error.UIErrorNameEmpty
import com.taxapprf.taxapp.ui.error.UIErrorPasswordLength
import com.taxapprf.taxapp.ui.error.UIErrorPhoneEmpty
import java.net.SocketTimeoutException
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

fun Throwable.getErrorDescription(): Int {
    // TODO дебаг, удалить вывод ниже
    println(this.message)
    println(this)

    return when (this) {
        is SocketTimeoutException -> R.string.data_error_socket_timeout
        is DataErrorUser -> R.string.auth_error
        is DataErrorInternal -> R.string.data_error_internal
        is DataErrorExternal -> R.string.data_external_error
        is DataErrorExcel -> R.string.data_error_excel
        is UIErrorNameEmpty -> R.string.error_name_empty
        is UIErrorPhoneEmpty -> R.string.error_phone_empty
        is UIErrorEmailEmpty -> R.string.error_email_empty
        is UIErrorEmailIncorrect -> R.string.error_email_incorrect
        is UIErrorPasswordLength -> R.string.error_password_length
        is DataErrorUserWrongPassword -> R.string.error_sign_in
        is DataErrorUserEmailAlreadyUse -> R.string.sign_up_error_email_already_use
        else -> throw this
    }
}

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