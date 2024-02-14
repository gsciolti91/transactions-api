package com.gsciolti.transactionsapi.domain.transaction.validate

import arrow.core.Either
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction
import com.gsciolti.transactionsapi.domain.transaction.UnvalidatedTransaction
import java.time.Instant

class TransactionIsRecent(lastSeconds: Long, end: () -> Instant) : ValidateTransaction {

    private val validate =
        TransactionIsAfter { end().minusSeconds(lastSeconds) }
            .and(TransactionIsBefore(end))

    override fun invoke(transaction: UnvalidatedTransaction): Either<CreateTransaction.Error, UnvalidatedTransaction> =
        validate(transaction)
}