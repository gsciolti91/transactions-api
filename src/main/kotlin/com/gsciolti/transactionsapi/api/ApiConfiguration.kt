package com.gsciolti.transactionsapi.api

import com.gsciolti.transactionsapi.domain.statistics.GetAggregatedStatistics
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction
import com.gsciolti.transactionsapi.domain.transaction.DeleteAllTransactions
import com.gsciolti.transactionsapi.repository.LatestTransactionsRepository
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