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
    private val saveTransaction: SaveTransaction
) : (UnvalidatedTransaction) -> Either<CreateTransaction.Error, Transaction> {

    override fun invoke(unvalidatedTransaction: UnvalidatedTransaction): Either<Error, Transaction> {
        val now = Instant.now()
        val lowerBound = now.minusSeconds(60)

        return unvalidatedTransaction
            .validate(isBetween(lowerBound, now))
            .map { Transaction(it.amount, it.date) }
            .flatMap {
                saveTransaction(it)
                    .mapLeft { TODO("Handle save errors") }
            }
    }

    private fun isBetween(start: Instant, end: Instant) = { transaction: UnvalidatedTransaction ->
        transaction.validate(isAfter(start), isBefore(end))
    }

    private fun isAfter(timestamp: Instant): (UnvalidatedTransaction) -> Either<Error, UnvalidatedTransaction> =
        { transaction: UnvalidatedTransaction ->
            if (transaction.date.isAfter(timestamp))
                transaction.right()
            else
                TransactionIsTooOld.left()
        }

    private fun isBefore(timestamp: Instant): (UnvalidatedTransaction) -> Either<Error, UnvalidatedTransaction> =
        { transaction: UnvalidatedTransaction ->
            if (transaction.date.isBefore(timestamp))
                transaction.right()
            else
                TransactionIsInTheFuture.left()
        }

    sealed class Error {
        object TransactionIsTooOld : Error()
        object TransactionIsInTheFuture : Error()
    }
}


