package com.gsciolti.transactionsapi.api.statistics

import com.gsciolti.transactionsapi.domain.statistics.GetAggregatedStatistics
import com.gsciolti.transactionsapi.domain.statistics.Statistics
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/statistics")
open class StatisticsController(
    private val getAggregatedStatistics: GetAggregatedStatistics
) {

    @GetMapping
    fun getAStatistics(): ResponseEntity<Any> =
        getAggregatedStatistics()
            .fold(
                { ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() },
                { ResponseEntity.ok(it.toResponse()) })
}

private fun Statistics.toResponse(): StatisticsResponse {
    return StatisticsResponse(
        sum.value.toPlainString(),
        avg.value.toPlainString(),
        max.value.toPlainString(),
        min.value.toPlainString(),
        count
    )
}
