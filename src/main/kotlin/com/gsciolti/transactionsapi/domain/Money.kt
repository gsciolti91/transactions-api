package com.gsciolti.transactionsapi.domain

import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.util.Currency

data class Money private constructor(
    val value: BigDecimal,
    val currency: Currency
) {
    constructor(value: String, currency: String) : this(
        BigDecimal(value).setScale(2, HALF_UP),
        Currency.getInstance(currency)
    )

    companion object {
        fun eur(value: String) = Money(value, "EUR")
    }

    operator fun plus(other: Money): Money =
        Money(this.value + other.value, currency)

    operator fun div(divisor: Long): Money =
        Money(value.divide(BigDecimal(divisor), 2, HALF_UP), currency)

    operator fun compareTo(other: Money): Int =
        this.value.compareTo(other.value)
}