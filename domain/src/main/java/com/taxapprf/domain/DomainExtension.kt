package com.taxapprf.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val PATTERN_DATE = "dd/MM/uuuu"
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_DATE)

fun Long.toAppDate(): String =
    LocalDate.ofEpochDay(this).format(formatter)

private const val RF_TAX_BASE = 0.13
private const val RF_TAX_LUXURY = 0.15
private const val RF_SUM_LUXURY = 5_000_000
fun Double.toTaxRF(yearTax: Double) =
    this * (if (yearTax > RF_SUM_LUXURY) RF_TAX_LUXURY else RF_TAX_BASE)