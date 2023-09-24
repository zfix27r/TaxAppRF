package com.taxapprf.data.remote.cbr.entity

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Valute", strict = false)
data class RemoteValuteEntity constructor(
    @field:Element(name = "NumCode", required = true)
    var numCode: Int? = null,

    @field:Element(name = "CharCode", required = true)
    var charCode: String? = null,

    @field:Element(name = "Nominal", required = true)
    var nominal: Int? = null,

    @field:Element(name = "Name", required = true)
    var name: String? = null,

    @field:Element(name = "Value", required = true)
    var value: String? = null
)
