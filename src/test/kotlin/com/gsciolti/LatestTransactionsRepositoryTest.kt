package com.gsciolti

import arrow.core.right
import com.gsciolti.Money.Companion.eur
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.Thread.sleep
import java.time.Instant.now
import kotlin.random.Random

class LatestTransactionsRepositoryTest {

    private val repository = LatestTransactionsRepository(seconds = 60)

    @Test
    fun `it should get aggregated statistics`() {

        repository.save(Transaction(eur("10.05"), now().minusMillis(500)))
        repository.save(Transaction(eur("5.50"), now().minusMillis(1500)))

        val statistics = repository.getAggregatedStatistics()

        assertEquals(
            Statistics(
                sum = eur("15.55"),
                avg = eur("7.78"),
                max = eur("10.05"),
                min = eur("5.50"),
                count = 2
            ).right(), statistics
        )
    }

    @Test
    fun `it should only keep last x seconds of transactions`() {

        val repository = LatestTransactionsRepository(seconds = 2)

        repository.save(Transaction(eur("5"), now().minusMillis(1500)))

        sleep(1100)

        val statistics = repository.getAggregatedStatistics()

        assertEquals(
            Statistics(
                sum = eur("0"),
                avg = eur("0"),
                max = eur("0"),
                min = eur("0"),
                count = 0
            ).right(), statistics
        )
    }

    @Test
    fun `it should delete all transactions`() {

        repository.save(Transaction(eur("10.05"), now()))

        repository.deleteAll()

        val statistics = repository.getAggregatedStatistics()

        assertEquals(
            Statistics(
                sum = eur("0"),
                avg = eur("0"),
                max = eur("0"),
                min = eur("0"),
                count = 0
            ).right(), statistics
        )
    }

    @Test
    fun `it should be thread safe`() {

        val last10Seconds = 10L
        val repository = LatestTransactionsRepository(seconds = last10Seconds)

        val threads = listOf(
            Thread(repository.save1000Eur(last10Seconds)),
            Thread(repository.save1000Eur(last10Seconds)),
            Thread(repository.save1000Eur(last10Seconds)),
            Thread(repository.save1000Eur(last10Seconds)),
            Thread(repository.save1000Eur(last10Seconds))
        )

        threads.forEach(Thread::start)
        threads.forEach(Thread::join)

        val statistics = repository.getAggregatedStatistics()

        assertEquals(
            Statistics(
                sum = eur("5000"),
                avg = eur("1"),
                max = eur("1"),
                min = eur("1"),
                count = 5000
            ).right(), statistics
        )
    }

    private fun LatestTransactionsRepository.save1000Eur(seconds: Long): () -> Unit = {
        repeat(1000) {
            save(Transaction(eur("1"), now().minusSeconds(Random.nextLong(seconds))))
        }
    }
}
