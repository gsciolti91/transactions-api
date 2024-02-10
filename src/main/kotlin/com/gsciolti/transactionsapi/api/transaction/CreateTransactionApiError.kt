package com.gsciolti.transactionsapi.api.transaction

import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction

sealed class CreateTransactionApiError {
    object InvalidJson : CreateTransactionApiError()
    data class CreateTransactionError(val cause: CreateTransaction.Error) : CreateTransactionApiError()
}