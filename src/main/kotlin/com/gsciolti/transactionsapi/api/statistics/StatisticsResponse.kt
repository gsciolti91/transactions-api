package com.gsciolti.transactionsapi.api.statistics

data class StatisticsResponse(
    val sum: String,
    val avg: String,
    val max: String,
    val min: String,
    val count: Long
)