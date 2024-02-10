package com.gsciolti.transactionsapi.repository.shiftingmap

import java.time.Duration
import java.util.concurrent.Executors.newScheduledThreadPool
import java.util.concurrent.TimeUnit.MILLISECONDS

class PacedShiftingMap<K, V> internal constructor(
    private val delegate: ShiftingMap<K, V>,
    shiftingInterval: Duration,
    afterShift: ShiftingMap<K, V>.() -> Unit
) : ShiftingMap<K, V> {

    private val scheduler = newScheduledThreadPool(1)

    init {
        scheduler.scheduleWithFixedDelay(
            {
                shiftBackward()
                afterShift(delegate)
            },
            shiftingInterval.toMillis(),
            shiftingInterval.toMillis(),
            MILLISECONDS
        )
    }

    override fun get(key: K): V? =
        delegate[key]

    override fun first(): V =
        delegate.first()

    override fun last(): V =
        delegate.last()

    override fun shiftBackward() {
        delegate.shiftBackward()
    }

    override fun values(): Collection<V> =
        delegate.values()
}

fun <K, V> ShiftingMap<K, V>.shiftingEvery(shiftingInterval: Duration, afterShift: ShiftingMap<K, V>.() -> Unit = {}) =
    PacedShiftingMap(this, shiftingInterval, afterShift)
