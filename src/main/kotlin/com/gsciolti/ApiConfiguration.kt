package com.gsciolti

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ApiConfiguration {

    @Bean
    open fun createTransaction(): CreateTransaction {
        return CreateTransaction(SaveTransactionInMemory())
    }

    @Bean
    open fun getStatistics(): GetAggregatedStatistics {
        return GetLastMinuteAggregatedStatistics()
    }
}