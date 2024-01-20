package com.gsciolti.transactionsapi.repository.shiftingmap

class IndexedShiftingHashMap<V : Any>(size: Long, init: (Long) -> V) :
    ShiftingHashMap<Long, V>(0L to init(0), *others(size, init)) {

    companion object {
        private fun <V : Any> others(size: Long, init: (Long) -> V): Array<Pair<Long, V>> =
            (1..<size)
                .map { i -> i to init(i) }
                .toTypedArray<Pair<Long, V>>()
    }
}
