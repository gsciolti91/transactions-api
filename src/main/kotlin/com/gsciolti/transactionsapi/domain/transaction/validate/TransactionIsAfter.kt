package com.gsciolti.transactionsapi.domain.transaction.validate

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gsciolti.transactionsapi.domain.transaction.UnvalidatedTransaction
import com.gsciolti.transactionsapi.domain.transaction.create.CreateTransaction
import java.time.Instant

class TransactionIsAfter(private val timestamp: () -> Instant) : ValidateTransaction {
    override fun invoke(transaction: UnvalidatedTransaction): Either<CreateTransaction.Error, UnvalidatedTransaction> =
        if (transaction.date.isAfter(timestamp()))
            transaction.right()
        else
            CreateTransaction.Error.TransactionIsTooOld.left()
}