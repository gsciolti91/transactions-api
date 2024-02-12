package com.gsciolti.transactionsapi.domain.transaction

import arrow.core.Either

interface SaveTransaction {

    fun save(transaction: Transaction): Either<Error, Transaction>

    sealed class Error
}