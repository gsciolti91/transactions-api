package com.gsciolti

import arrow.core.Either
import arrow.core.right
import com.gsciolti.Money.Companion.eur
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.Duration
import java.time.Instant

class LatestTransactionsRepository(seconds: Long) : SaveTransaction, GetAggregatedStatistics {

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

                    if (min == null && partialTransactions.min != null || min != null && partialTransactions.min != null && partialTransactions.min!!.value < min!!.value) {
                        min = partialTransactions.min
                    }

                    if (max == null && partialTransactions.max != null || max != null && partialTransactions.max != null && partialTransactions.max!!.value > max!!.value) {
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
                    // todo feature envy
                    avg =
                    if (total.count != 0L)
                        eur(total.sum.value.divide(BigDecimal(total.count), 2, HALF_UP).toPlainString())
                    else eur("0")
                )
            }
            .right()
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