package com.gsciolti

import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
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
                { status(INTERNAL_SERVER_ERROR).build() },
                { ok(it.toResponse()) })
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
