package com.gsciolti.transactionsapi.domain.transaction.validate

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction
import com.gsciolti.transactionsapi.domain.transaction.UnvalidatedTransaction
import java.time.Instant

class TransactionIsBefore(private val timestamp: () -> Instant) : ValidateTransaction {
    override fun invoke(transaction: UnvalidatedTransaction): Either<CreateTransaction.Error, UnvalidatedTransaction> =
        if (transaction.date.isBefore(timestamp()))
            transaction.right()
        else
            CreateTransaction.Error.TransactionIsInTheFuture.left()
}