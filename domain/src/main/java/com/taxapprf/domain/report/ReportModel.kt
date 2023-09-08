package com.taxapprf.domain.report

import com.taxapprf.domain.Sync

data class ReportModel(
    val id: Int,
    override val key: String,
    val tax: Double,
    val size: Int,
    override val isSync: Boolean,
    override val isDelete: Boolean,
    override val syncAt: Long,
) : Sync