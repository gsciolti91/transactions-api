package com.gsciolti

import arrow.core.Either

interface GetAggregatedStatistics : () -> Either<GetAggregatedStatistics.Error, Statistics> {
    sealed class Error
}
