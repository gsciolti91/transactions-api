package com.gsciolti.transactionsapi.repository.shiftingmap

interface ShiftingMap<K, V> {

    operator fun get(key: K): V?

    fun first(): V

    fun last(): V

    fun shiftBackward()

    fun values(): Collection<V>
}
