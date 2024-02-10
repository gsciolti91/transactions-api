package com.gsciolti.transactionsapi.domain.transaction

import arrow.core.Either

interface DeleteAllTransactions {

    fun deleteAll(): Either<Error, Unit>

    sealed class Error
}