package com.gsciolti

data class StatisticsResponse(
    val sum: String,
    val avg: String,
    val max: String,
    val min: String,
    val count: Long
)