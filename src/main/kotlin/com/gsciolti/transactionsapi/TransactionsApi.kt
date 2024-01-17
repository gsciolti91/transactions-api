package com.gsciolti.transactionsapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class TransactionsApi

fun main(args: Array<String>) {
    runApplication<TransactionsApi>(*args)
}
