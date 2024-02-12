package com.gsciolti.transactionsapi.repository

import arrow.core.Either
import arrow.core.right
import com.gsciolti.transactionsapi.domain.Money
import com.gsciolti.transactionsapi.domain.Money.Companion.eur
import com.gsciolti.transactionsapi.domain.statistics.GetAggregatedStatistics
import com.gsciolti.transactionsapi.domain.statistics.Statistics
import com.gsciolti.transactionsapi.domain.transaction.DeleteAllTransactions
import com.gsciolti.transactionsapi.domain.transaction.SaveTransaction
import com.gsciolti.transactionsapi.domain.transaction.Transaction
import com.gsciolti.transactionsapi.domain.transaction.create.SaveTransaction
import com.gsciolti.transactionsapi.domain.transaction.delete.DeleteAllTransactions
import com.gsciolti.transactionsapi.repository.shiftingmap.IndexedShiftingHashMap
import com.gsciolti.transactionsapi.repository.shiftingmap.shiftingEvery
import java.time.Duration
import java.time.Instant

class LatestTransactionsRepository(
    private val timestampUpperBound: () -> Instant,
    timestampOffsetSeconds: Long
) : SaveTransaction, GetAggregatedStatistics, DeleteAllTransactions {

    private val aggregatedTransactions =
        IndexedShiftingHashMap(timestampOffsetSeconds) { AggregatedTransactions.empty() }
            .shiftingEvery(Duration.ofSeconds(1)) { first().clear() }

    override fun save(transaction: Transaction): Either<SaveTransaction.Error, Transaction> {

        val index = Duration.between(transaction.timestamp, timestampUpperBound()).seconds

        aggregatedTransactions[index]?.add(transaction)

        return transaction.right()
    }

    override fun getAggregatedStatistics(): Either<GetAggregatedStatistics.Error, Statistics> =
        aggregatedTransactions
            .values()
            .filter { it.count != 0L }
            .fold(AggregatedTransactions.empty()) { total, partial ->
                total.apply {
                    sum += partial.sum

                    if (count == 0L || partial.min < min) {
                        min = partial.min
                    }

                    if (partial.max > max) {
                        max = partial.max
                    }

                    count += partial.count
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

    override fun deleteAll(): Either<DeleteAllTransactions.Error, Unit> {
        aggregatedTransactions.values().forEach(AggregatedTransactions::clear)
        return Unit.right()
    }

    private class AggregatedTransactions(
        var sum: Money,
        var max: Money,
        var min: Money,
        var count: Long
    ) {
        companion object {
            fun empty() = AggregatedTransactions(
                eur("0"),
                eur("0"),
                eur("0"),
                0
            )
        }

        fun add(transaction: Transaction) {
            synchronized(this) {
                sum += transaction.amount

                if (count == 0L || transaction.amount < min) {
                    min = transaction.amount
                }

                if (transaction.amount > max) {
                    max = transaction.amount
                }

                count++
            }
        }

        fun clear() {
            sum = eur("0")
            max = eur("0")
            min = eur("0")
            count = 0
        }
    }
}