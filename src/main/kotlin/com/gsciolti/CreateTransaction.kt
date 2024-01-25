package com.gsciolti

import arrow.core.Either
import java.time.Instant

class CreateTransaction(
    private val saveTransaction: SaveTransaction
) : (UnvalidatedTransaction) -> Either<CreateTransactionError, Transaction> {

    override fun invoke(unvalidatedTransaction: UnvalidatedTransaction): Either<CreateTransactionError, Transaction> {

        val transaction = Transaction(
            unvalidatedTransaction.amount,
            unvalidatedTransaction.date
        )

        return saveTransaction(transaction)
            .mapLeft { TODO("Handle save errors") }
    }
}

data class Transaction(
    val amount: Money,
    val timestamp: Instant
)

sealed class CreateTransactionError {

}

data class UnvalidatedTransaction internal constructor(
    val amount: Money,
    val date: Instant
)
