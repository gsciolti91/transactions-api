package com.gsciolti

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.flatMap
import com.gsciolti.CreateTransactionError.TransactionIsInTheFuture
import com.gsciolti.CreateTransactionError.TransactionIsTooOld
import com.gsciolti.Money.Companion.eur
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.status
import org.springframework.http.ResponseEntity.unprocessableEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController("/transactions")
open class TransactionsController(
    private val createTransaction: CreateTransaction,
    private val deleteAllTransactions: DeleteAllTransactions
) {
    @PostMapping
    fun createTransaction(@RequestBody request: CreateTransactionRequest): ResponseEntity<Any> =
        request
            .toUnvalidatedTransaction()
            .flatMap(createTransaction)
            .fold({
                when (it) {
                    is InvalidJson -> {
                        unprocessableEntity().build()
                    }

                    is CreateTransactionError -> when (it) {
                        TransactionIsInTheFuture -> unprocessableEntity().build()
                        TransactionIsTooOld -> noContent().build()
                    }

                    else -> status(INTERNAL_SERVER_ERROR).build()
                }
            }, {
                status(CREATED).build()
            })

    @DeleteMapping
    fun deleteAllTransactions(): ResponseEntity<Any> {

        deleteAllTransactions.deleteAll()

        return noContent().build()
    }
}

private fun CreateTransactionRequest.toUnvalidatedTransaction(): Either<InvalidJson, UnvalidatedTransaction> =
    catch { Instant.parse(timestamp) }
        .flatMap { timestamp ->
            catch { eur(amount) }
                .map { amount ->
                    UnvalidatedTransaction(amount, timestamp)
                }
        }
        .mapLeft { InvalidJson }

object InvalidJson
