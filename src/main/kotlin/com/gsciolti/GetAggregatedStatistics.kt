package com.gsciolti

import arrow.core.Either

interface GetAggregatedStatistics {

    operator fun invoke(): Either<Error, Statistics>

    sealed class Error
}
