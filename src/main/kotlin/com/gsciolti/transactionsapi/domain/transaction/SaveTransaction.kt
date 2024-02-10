package com.gsciolti.transactionsapi.domain.transaction

import arrow.core.Either

interface SaveTransaction {

    operator fun invoke(transaction: Transaction): Either<Error, Transaction>

    sealed class Error
}