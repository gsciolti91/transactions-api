package com.gsciolti.transactionsapi.domain.transaction.validate

import arrow.core.Either
import arrow.core.flatMap
import com.gsciolti.transactionsapi.domain.transaction.UnvalidatedTransaction
import com.gsciolti.transactionsapi.domain.transaction.create.CreateTransaction

interface ValidateTransaction : (UnvalidatedTransaction) -> Either<CreateTransaction.Error, UnvalidatedTransaction> {

    fun and(other: ValidateTransaction): ValidateTransaction {
        val first = this
        return object : ValidateTransaction {
            override fun invoke(transaction: UnvalidatedTransaction): Either<CreateTransaction.Error, UnvalidatedTransaction> =
                first(transaction).flatMap { other(transaction) }
        }
    }
}