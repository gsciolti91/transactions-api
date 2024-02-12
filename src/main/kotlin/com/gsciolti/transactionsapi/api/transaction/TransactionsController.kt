package com.gsciolti.transactionsapi.api.transaction

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.flatMap
import com.gsciolti.transactionsapi.api.transaction.CreateTransactionApiError.CreateTransactionError
import com.gsciolti.transactionsapi.api.transaction.CreateTransactionApiError.InvalidJson
import com.gsciolti.transactionsapi.domain.Money.Companion.eur
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction.Error.TransactionIsInTheFuture
import com.gsciolti.transactionsapi.domain.transaction.CreateTransaction.Error.TransactionIsTooOld
import com.gsciolti.transactionsapi.domain.transaction.DeleteAllTransactions
import com.gsciolti.transactionsapi.domain.transaction.UnvalidatedTransaction
import org.springframework.http.HttpStatus.CREATED
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
    createTransaction: CreateTransaction,
    deleteAllTransactions: DeleteAllTransactions
) {
    @PostMapping
    fun createTransaction(@RequestBody request: CreateTransactionRequest): ResponseEntity<*> =
        request
            .toUnvalidatedTransaction()
            .flatMap(createTransaction)
            .fold(handleCreateTransactionError) { status(CREATED).build() }

    @DeleteMapping
    fun deleteTransactions(): ResponseEntity<Any> {
        deleteAllTransactions()
        return noContent().build()
    }

    private val createTransaction = { transaction: UnvalidatedTransaction ->
        createTransaction(transaction)
            .mapLeft(::CreateTransactionError)
    }

    private val handleCreateTransactionError = { error: CreateTransactionApiError ->
        when (error) {
            is InvalidJson -> unprocessableEntity().build<CreateTransactionApiError>()

            is CreateTransactionError -> when (error.cause) {
                TransactionIsInTheFuture -> unprocessableEntity().build()
                TransactionIsTooOld -> noContent().build()
            }
        }
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

