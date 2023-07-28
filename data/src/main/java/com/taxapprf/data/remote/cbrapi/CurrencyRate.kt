package com.taxapprf.data.remote.cbrapi

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root


@Root(name = "Valute", strict = false)
data class CurrencyRate(
    @Element(name = "CharCode")
    val charCode: String? = null,
    @Element(name = "Nominal")
    val nominal: Int? = 0,

    @Element(name = "Name")
    val name: String? = null,

    @Element(name = "Value")
    val value: String? = null
) {
    fun getValue() = value!!.replace(',', '.').toDouble()
}