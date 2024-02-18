package com.gsciolti.transactionsapi.domain

import com.gsciolti.transactionsapi.domain.Money.Companion.eur
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class MoneyTest {

    @Test
    fun `should round amount`() {
        assertEquals(BigDecimal("1.00"), eur("1").value)
        assertEquals(BigDecimal("10.30"), eur("10.3").value)
        assertEquals(BigDecimal("10.34"), eur("10.344").value)
        assertEquals(BigDecimal("10.35"), eur("10.345").value)
        assertEquals(BigDecimal("10.35"), eur("10.346").value)
    }

    @Test
    fun sum() {
        assertEquals(eur("16.10"), eur("10.50") + eur("5.60"))
    }

    @Test
    fun divide() {
        assertEquals(eur("1.63"), eur("3.25") / 2)
        assertEquals(eur("5.39"), eur("10.77") / 2)
        assertEquals(eur("1.67"), eur("5") / 3)
    }

    @Test
    fun greaterThan() {
        assertTrue(eur("16.10") > eur("16"))
        assertFalse(eur("16.10") > eur("16.10"))
        assertFalse(eur("16.10") > eur("16.20"))
    }

    @Test
    fun lessThan() {
        assertTrue(eur("16.10") < eur("16.20"))
        assertFalse(eur("16.10") < eur("16.10"))
        assertFalse(eur("16.10") < eur("16"))
    }

    @Test
    fun min() {
        assertEquals(eur("1"), Money.min(eur("1"), eur("5"), eur("2")))
    }

    @Test
    fun max() {
        assertEquals(eur("5"), Money.max(eur("1"), eur("5"), eur("2")))
    }
}