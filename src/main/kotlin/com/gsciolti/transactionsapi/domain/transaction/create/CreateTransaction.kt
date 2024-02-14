package com.gsciolti.transactionsapi.domain.transaction.create

import arrow.core.Either
import arrow.core.flatMap
import com.gsciolti.transactionsapi.domain.transaction.Transaction
import com.gsciolti.transactionsapi.domain.transaction.UnvalidatedTransaction
import com.gsciolti.transactionsapi.domain.transaction.validate.ValidateTransaction

class CreateTransaction(
    private val validate: ValidateTransaction,
    saveTransaction: SaveTransaction
) : (UnvalidatedTransaction) -> Either<CreateTransaction.Error, Transaction> {

    override fun invoke(unvalidatedTransaction: UnvalidatedTransaction): Either<Error, Transaction> =
        validate(unvalidatedTransaction)
            .map { Transaction(it.amount, it.date) }
            .flatMap(saveTransaction)

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
