package com.taxapprf.taxapp.ui

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.taxapprf.domain.PATTERN_DATE
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.floor

val state: MutableSharedFlow<BaseState> = MutableSharedFlow()

fun <T> Flow<T>.showLoading() =
    this
        .onStart { state.emit(Loading) }
        .catch { state.emit(Error(it)) }
        .onEach { state.emit(Success) }

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
    val permissionWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE
    val permissionRead = Manifest.permission.READ_EXTERNAL_STORAGE
    return if (
        ContextCompat.checkSelfPermission(
            this,
            permissionWrite
        ) == PackageManager.PERMISSION_GRANTED
    ) true
    else {
        ActivityCompat.requestPermissions(this, arrayOf(permissionRead, permissionWrite), 1)
        false
    }
}

const val ROUND_TWO = 100.0
const val ROUND_SIX = 1_000_000.0

fun Double.round(k: Double = ROUND_TWO) = floor(this * k) / k

fun String.toLocalDate() =
    try {
        LocalDate.parse(
            this,
            DateTimeFormatter
                .ofPattern(PATTERN_DATE)
                .withLocale(Locale.ROOT)
                .withResolverStyle(ResolverStyle.STRICT)
        )
    } catch (e: Exception) {
        null
    }

fun String.showDatePickerDialog(
    context: Context,
    listener: DatePickerDialog.OnDateSetListener
) {
    toLocalDate()
        ?.let {
            DatePickerDialog(
                context, listener,
                it.year,
                it.monthValue - 1 ,
                it.dayOfMonth
            ).show()
        }
        ?: run {
            com.taxapprf.taxapp.ui.showDatePickerDialog(context, listener)
        }
}

fun showDatePickerDialog(
    context: Context,
    listener: DatePickerDialog.OnDateSetListener
) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context, listener,
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH] - 1,
        calendar[Calendar.DAY_OF_MONTH]
    ).show()
}

fun getEpochDay(year: Int, month: Int, dayOfMonth: Int) =
    LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()

fun Context.convertToTransactionTypeName(typeK: Int) =
    when (typeK) {
        TransactionType.TRADE.k -> getString(R.string.transaction_type_trade)
        TransactionType.COMMISSION.k -> getString(R.string.transaction_type_commission)
        TransactionType.FUNDING_WITHDRAWAL.k -> getString(R.string.transaction_type_funding_withdrawal)
        else -> null
    }

fun Context.convertToTransactionTypeK(typeName: String) =
    when (typeName) {
        getString(R.string.transaction_type_trade) -> TransactionType.TRADE.k
        getString(R.string.transaction_type_commission) -> TransactionType.COMMISSION.k
        getString(R.string.transaction_type_funding_withdrawal) -> TransactionType.FUNDING_WITHDRAWAL.k
        else -> null
    }