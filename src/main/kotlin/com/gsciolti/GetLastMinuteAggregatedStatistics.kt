package com.gsciolti

import arrow.core.Either
import arrow.core.right

class GetLastMinuteAggregatedStatistics : GetAggregatedStatistics {
    override fun invoke(): Either<GetAggregatedStatistics.Error, Statistics> {
        return Statistics(
            Money.eur("1000.00"),
            Money.eur("100.53"),
            Money.eur("200000.49"),
            Money.eur("50.23"),
            10L
        ).right()
    }
}