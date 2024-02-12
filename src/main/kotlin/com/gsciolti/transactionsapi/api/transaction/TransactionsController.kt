package com.gsciolti.transactionsapi.api.transaction

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.flatMap
import com.gsciolti.transactionsapi.api.transaction.CreateTransactionApiError.InvalidJson
import com.gsciolti.transactionsapi.domain.Money.Companion.eur
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction.Error.TransactionIsInTheFuture
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction.Error.TransactionIsTooOld
import com.gsciolti.transactionsapi.domain.transaction.DeleteAllTransactions
import com.gsciolti.transactionsapi.domain.transaction.UnvalidatedTransaction
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
    deleteAllTransactions: DeleteAllTransactions
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

                    is CreateTransaction.Error -> when (it) {
                        TransactionIsInTheFuture -> unprocessableEntity().build()
                        TransactionIsTooOld -> noContent().build()
                    }

                    else -> status(INTERNAL_SERVER_ERROR).build()
                }
            }, {
                status(CREATED).build()
            })

    @DeleteMapping
    fun deleteTransactions(): ResponseEntity<Any> {

        deleteAllTransactions()

        return noContent().build()
    }

    private val deleteAllTransactions = deleteAllTransactions::deleteAll
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
