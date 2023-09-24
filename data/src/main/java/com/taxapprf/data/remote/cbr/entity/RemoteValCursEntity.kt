package com.taxapprf.data.remote.cbr.entity

import com.taxapprf.data.remote.cbr.entity.RemoteValuteEntity
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ValCurs", strict = false)
data class RemoteValCursEntity constructor(
    @field:ElementList(
        required = false,
        name = "Valute",
        entry = "Valute",
        inline = true,
        empty = true
    )
    var currencies: MutableList<RemoteValuteEntity>? = null
)