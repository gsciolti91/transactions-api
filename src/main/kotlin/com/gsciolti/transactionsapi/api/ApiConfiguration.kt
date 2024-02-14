package com.gsciolti.transactionsapi.api

import com.gsciolti.transactionsapi.domain.statistics.GetAggregatedStatistics
import com.gsciolti.transactionsapi.domain.transaction.create.CreateTransaction
import com.gsciolti.transactionsapi.domain.transaction.delete.DeleteAllTransactions
import com.gsciolti.transactionsapi.domain.transaction.validate.TransactionIsRecent
import com.gsciolti.transactionsapi.repository.LatestTransactionsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Instant

@Configuration
open class ApiConfiguration {

    private val timestampUpperBound: () -> Instant = { Instant.now() }
    private val timestampOffsetSeconds: Long = 60
    private val validateTransaction = TransactionIsRecent(timestampOffsetSeconds, timestampUpperBound)
    private val transactionsRepository = LatestTransactionsRepository(timestampOffsetSeconds, timestampUpperBound)

    @Bean
    open fun createTransaction(): CreateTransaction = CreateTransaction(validateTransaction, transactionsRepository)

    @Bean
    open fun getStatistics(): GetAggregatedStatistics = transactionsRepository

    @Bean
    open fun deleteAllTransactions(): DeleteAllTransactions = transactionsRepository
}