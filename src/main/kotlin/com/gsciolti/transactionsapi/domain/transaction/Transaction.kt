package com.gsciolti.transactionsapi.domain.transaction

import com.gsciolti.transactionsapi.domain.Money
import java.time.Instant

data class Transaction internal constructor(
    val amount: Money,
    val timestamp: Instant
)