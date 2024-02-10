package com.gsciolti.transactionsapi.domain.statistics

import arrow.core.Either

interface GetAggregatedStatistics {

    operator fun invoke(): Either<Error, Statistics>

    sealed class Error
}
