package com.gsciolti

import arrow.core.Either

interface SaveTransaction : (Transaction) -> Either<SaveTransaction.Error, Transaction> {
    sealed class Error
}