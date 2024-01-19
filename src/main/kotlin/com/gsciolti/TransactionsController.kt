package com.gsciolti

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.Instant

@RestController("/transactions")
open class TransactionsController(
    private val createTransaction: CreateTransaction
) {
    @PostMapping
    fun createTransaction(@RequestBody request: CreateTransactionRequest): ResponseEntity<Any> =
        request
            .toUnvalidatedTransaction()
            .flatMap(createTransaction)
            .fold({
                TODO("Handle create transaction errors")
            }, {
                status(CREATED).build()
            })

    @DeleteMapping
    fun deleteAllTransactions(): ResponseEntity<Any> {
        return noContent().build()
    }
}

private fun CreateTransactionRequest.toUnvalidatedTransaction(): Either<ParseTransactionRequestError, UnvalidatedTransaction> {
    return UnvalidatedTransaction(
        Money(BigDecimal(amount)), // todo factory method
        Instant.parse(timestamp)
    ).right()
}

class ParseTransactionRequestError {

}
