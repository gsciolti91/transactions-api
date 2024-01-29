package com.gsciolti

import arrow.core.Either

interface DeleteAllTransactions {

    fun deleteAll(): Either<Error, Unit>

    sealed class Error
}