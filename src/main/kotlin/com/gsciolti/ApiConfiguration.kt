package com.gsciolti

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ApiConfiguration {

    private val transactionsRepository = LatestTransactionsRepository(seconds = 60)

    @Bean
    open fun createTransaction(): CreateTransaction {
        return CreateTransaction(transactionsRepository)
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