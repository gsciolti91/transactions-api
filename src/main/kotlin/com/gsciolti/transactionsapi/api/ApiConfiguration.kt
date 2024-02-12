package com.gsciolti.transactionsapi.api

import com.gsciolti.transactionsapi.domain.statistics.GetAggregatedStatistics
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction
import com.gsciolti.transactionsapi.domain.transaction.DeleteAllTransactions
import com.gsciolti.transactionsapi.repository.LatestTransactionsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Instant

@Configuration
open class ApiConfiguration {

    private val timestampUpperBound: () -> Instant = { Instant.now() }
    private val timestampOffsetSeconds: Long = 60
    private val transactionsRepository = LatestTransactionsRepository(timestampUpperBound, timestampOffsetSeconds)

    @Bean
    open fun createTransaction(): CreateTransaction {
        return CreateTransaction(timestampUpperBound, timestampOffsetSeconds, transactionsRepository)
    }

    @Bean
    open fun getStatistics(): GetAggregatedStatistics {
        return transactionsRepository
    }

    @Bean
    open fun deleteAllTransactions(): DeleteAllTransactions {
        return transactionsRepository
    }
}