package com.gsciolti.transactionsapi.domain.transaction.validate

import arrow.core.left
import arrow.core.right
import com.gsciolti.transactionsapi.domain.Money.Companion.eur
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction.Error.TransactionIsInTheFuture
import com.gsciolti.transactionsapi.domain.transaction.UnvalidatedTransaction
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class ValidateTransactionTest {

    private val validatePass = object : ValidateTransaction {
        override fun invoke(transaction: UnvalidatedTransaction) = transaction.right()
    }

    private val validateFail = object : ValidateTransaction {
        override fun invoke(transaction: UnvalidatedTransaction) = error.left()
    }

    @Test
    fun `it should and validations`() {
        assertEquals(transaction.right(), validatePass.and(validatePass)(transaction))
        assertEquals(error.left(), validatePass.and(validateFail)(transaction))
        assertEquals(error.left(), validateFail.and(validatePass)(transaction))
        assertEquals(error.left(), validateFail.and(validateFail)(transaction))
    }

    private val transaction = UnvalidatedTransaction(eur("10"), Instant.ofEpochMilli(3000001))
    private val error = TransactionIsInTheFuture
}