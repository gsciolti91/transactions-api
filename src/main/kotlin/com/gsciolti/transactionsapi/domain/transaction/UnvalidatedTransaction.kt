package com.gsciolti.transactionsapi.domain.transaction

import com.gsciolti.transactionsapi.domain.Money
import java.time.Instant

data class UnvalidatedTransaction(
    val amount: Money,
    val date: Instant
)