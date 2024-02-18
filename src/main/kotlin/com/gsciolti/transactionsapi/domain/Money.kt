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

        fun min(first: Money, vararg others: Money): Money =
            others.fold(first) { min, next -> if (next < min) next else min }

        fun max(first: Money, vararg others: Money): Money =
            others.fold(first) { max, next -> if (next > max) next else max }
    }

    operator fun plus(other: Money): Money =
        Money(this.value + other.value, currency)

    operator fun div(divisor: Long): Money =
        Money(value.divide(BigDecimal(divisor), 2, HALF_UP), currency)

    operator fun compareTo(other: Money): Int =
        this.value.compareTo(other.value)
}