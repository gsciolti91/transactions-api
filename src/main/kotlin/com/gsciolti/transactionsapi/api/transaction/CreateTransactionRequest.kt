package com.gsciolti.transactionsapi.api.transaction

data class CreateTransactionRequest(
    val amount: String,
    val timestamp: String
)
