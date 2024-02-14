package com.gsciolti.transactionsapi.domain.transaction.validate

import arrow.core.left
import arrow.core.right
import com.gsciolti.transactionsapi.domain.Money.Companion.eur
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction.Error.TransactionIsInTheFuture
import com.gsciolti.transactionsapi.domain.transaction.UnvalidatedTransaction
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class TransactionIsBeforeTest {

    private val validate = TransactionIsBefore { Instant.ofEpochMilli(3000000) }

    @Test
    fun `it should pass if the transaction is before the timestamp`() {
        val transaction = UnvalidatedTransaction(eur("10"), Instant.ofEpochMilli(2999999))

        assertEquals(transaction.right(), validate(transaction))
    }

    @Test
    fun `it should fail if the transaction is at the timestamp`() {
        val transaction = UnvalidatedTransaction(eur("10"), Instant.ofEpochMilli(3000000))

        assertEquals(TransactionIsInTheFuture.left(), validate(transaction))
    }

    @Test
    fun `it should fail if the transaction is after the timestamp`() {
        val transaction = UnvalidatedTransaction(eur("10"), Instant.ofEpochMilli(3000001))

        assertEquals(TransactionIsInTheFuture.left(), validate(transaction))
    }
}