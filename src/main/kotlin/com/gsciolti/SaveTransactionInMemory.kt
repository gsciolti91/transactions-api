package com.gsciolti

import arrow.core.Either
import arrow.core.right

class SaveTransactionInMemory : SaveTransaction {

    override fun invoke(transaction: Transaction): Either<SaveTransaction.Error, Transaction> {
        return transaction.right()
    }
}