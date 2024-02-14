package com.gsciolti.transactionsapi.domain.transaction.create

import arrow.core.Either
import com.gsciolti.transactionsapi.domain.transaction.Transaction

interface SaveTransaction {

    fun save(transaction: Transaction): Either<Error, Transaction>

    sealed class Error
}