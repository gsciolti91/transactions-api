package com.gsciolti.transactionsapi.api.statistics

import com.gsciolti.transactionsapi.domain.statistics.GetAggregatedStatistics
import com.gsciolti.transactionsapi.domain.statistics.Statistics
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/statistics")
open class StatisticsController(
    getAggregatedStatistics: GetAggregatedStatistics
) {
    @GetMapping
    fun getStatistics(): ResponseEntity<Any> =
        getAggregatedStatistics()
            .fold(
                { status(HttpStatus.INTERNAL_SERVER_ERROR).build() },
                { ok(it.toResponse()) })

    private val getAggregatedStatistics = getAggregatedStatistics::getAggregatedStatistics
}

private fun Statistics.toResponse(): StatisticsResponse {
    return StatisticsResponse(
        sum.value.toString(),
        avg.value.toString(),
        max.value.toString(),
        min.value.toString(),
        count
    )
}
