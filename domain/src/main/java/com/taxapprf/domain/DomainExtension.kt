package com.taxapprf.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val PATTERN_DATE = "dd/MM/uuuu"
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_DATE)

fun Long.toAppDate(): String =
    LocalDate.ofEpochDay(this).format(formatter)