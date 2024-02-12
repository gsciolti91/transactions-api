package com.gsciolti.transactionsapi.domain.statistics

import arrow.core.Either

interface GetAggregatedStatistics {

    fun getAggregatedStatistics(): Either<Error, Statistics>

    sealed class Error
}
