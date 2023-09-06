package com.taxapprf.domain.report

import com.taxapprf.domain.Sync

data class ReportModel(
    override val key: String,
    val tax: Double,
    val size: Int,
    override val isSync: Boolean,
    override val isDeferredDelete: Boolean,
    override val syncAt: Long,
) : Sync