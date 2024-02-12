package com.gsciolti.transactionsapi.domain.transaction

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction.Error.TransactionIsInTheFuture
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction.Error.TransactionIsTooOld
import com.gsciolti.transactionsapi.functional.EitherExtensions.validate
import java.time.Instant

class CreateTransaction(
    private val timestampUpperBound: () -> Instant,
    private val timestampOffsetSeconds: Long,
    saveTransaction: SaveTransaction
) : (UnvalidatedTransaction) -> Either<CreateTransaction.Error, Transaction> {

    override fun invoke(unvalidatedTransaction: UnvalidatedTransaction): Either<Error, Transaction> {
        val upperBound = timestampUpperBound()
        val lowerBound = upperBound.minusSeconds(timestampOffsetSeconds)

        return unvalidatedTransaction
            .validate(isBetween(lowerBound, upperBound))
            .map { Transaction(it.amount, it.date) }
            .flatMap(saveTransaction)
    }

    private fun isBetween(start: Instant, end: Instant) = { transaction: UnvalidatedTransaction ->
        transaction.validate(isAfter(start), isBefore(end))
    }

    private fun isAfter(timestamp: Instant) = { transaction: UnvalidatedTransaction ->
        if (transaction.date.isAfter(timestamp))
            transaction.right()
        else
            TransactionIsTooOld.left()
    }

    private fun isBefore(timestamp: Instant) = { transaction: UnvalidatedTransaction ->
        if (transaction.date.isBefore(timestamp))
            transaction.right()
        else
            TransactionIsInTheFuture.left()
    }

    private val saveTransaction = { transaction: Transaction ->
        saveTransaction
            .save(transaction)
            .mapLeft { TODO("Handle save errors") }
    }

    sealed class Error {
        object TransactionIsTooOld : Error()
        object TransactionIsInTheFuture : Error()
    }
}


