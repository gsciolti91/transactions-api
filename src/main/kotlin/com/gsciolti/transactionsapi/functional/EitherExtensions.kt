package com.gsciolti.transactionsapi.functional

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right

object EitherExtensions {

    fun <L, R> R.validate(vararg checks: (R) -> Either<L, R>): Either<L, R> =
        checks.fold(this.right() as Either<L, R>) { current, nextCheck ->
            current.flatMap { nextCheck(this) }
        }
}