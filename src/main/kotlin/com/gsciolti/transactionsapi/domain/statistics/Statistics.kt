package com.gsciolti.transactionsapi.domain.statistics

import com.gsciolti.transactionsapi.domain.Money

data class Statistics(
    val sum: Money,
    val avg: Money,
    val max: Money,
    val min: Money,
    val count: Long
)
