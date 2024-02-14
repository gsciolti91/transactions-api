package com.gsciolti.transactionsapi.domain.transaction.delete

import arrow.core.Either

interface DeleteAllTransactions {

    fun deleteAll(): Either<Error, Unit>

    sealed class Error
}