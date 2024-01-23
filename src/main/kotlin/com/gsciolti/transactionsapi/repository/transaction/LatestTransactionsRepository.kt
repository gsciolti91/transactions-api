package com.gsciolti.transactionsapi.repository.transaction

import arrow.core.Either
import arrow.core.right
import com.gsciolti.transactionsapi.domain.Money
import com.gsciolti.transactionsapi.domain.Money.Companion.eur
import com.gsciolti.transactionsapi.domain.statistics.GetAggregatedStatistics
import com.gsciolti.transactionsapi.domain.statistics.Statistics
import com.gsciolti.transactionsapi.domain.transaction.DeleteAllTransactions
import com.gsciolti.transactionsapi.domain.transaction.SaveTransaction
import com.gsciolti.transactionsapi.domain.transaction.Transaction
import java.time.Duration
import java.time.Instant

class LatestTransactionsRepository(seconds: Long) : SaveTransaction, GetAggregatedStatistics, DeleteAllTransactions {

    private val aggregatedTransactions =
        ShiftingHashMap
            .indexed(seconds) { AggregatedTransactions.empty() }
            .shiftingEvery(Duration.ofSeconds(1)) { first().clear() }

    override fun invoke(transaction: Transaction) = save(transaction)
    override fun invoke() = getAggregatedStatistics()

    fun save(transaction: Transaction): Either<SaveTransaction.Error, Transaction> {

        val index = Duration.between(transaction.timestamp, Instant.now()).seconds

        // todo something about the null check?
        aggregatedTransactions[index]?.add(transaction)

        return transaction.right()
    }

    fun getAggregatedStatistics(): Either<GetAggregatedStatistics.Error, Statistics> {

        return aggregatedTransactions
            .values()
            .fold(AggregatedTransactions.empty()) { total, partialTransactions ->
                total.apply {
                    sum += partialTransactions.sum
                    count += partialTransactions.count

                    if (min == null && partialTransactions.min != null || min != null && partialTransactions.min != null && partialTransactions.min!! < min!!) {
                        min = partialTransactions.min
                    }

                    if (max == null && partialTransactions.max != null || max != null && partialTransactions.max != null && partialTransactions.max!! > max!!) {
                        max = partialTransactions.max
                    }
                }
            }
            .let { total ->
                Statistics(
                    sum = total.sum,
                    max = total.max,
                    min = total.min,
                    count = total.count,
                    avg = if (total.count != 0L) total.sum / total.count else eur("0")
                )
            }
            .right()
    }

    override fun deleteAll(): Either<DeleteAllTransactions.Error, Unit> {
        aggregatedTransactions.values().forEach(AggregatedTransactions::clear)
        return Unit.right()
    }

    private class AggregatedTransactions(
        var sum: Money,
        var max: Money?,
        var min: Money?,
        var count: Long
    ) {

        companion object {
            fun empty() = AggregatedTransactions(
                eur("0"),
                null,
                null,
                0
            )
        }

        fun add(transaction: Transaction) {
            synchronized(this) {
                sum += transaction.amount
                count++

                // todo feature envy
                if (min == null || transaction.amount.value < min!!.value) {
                    min = transaction.amount
                }

                if (max == null || transaction.amount.value > max!!.value) {
                    max = transaction.amount
                }
            }
        }

        fun clear() {
            sum = eur("0")
            max = null
            min = null
            count = 0
        }
    }
}