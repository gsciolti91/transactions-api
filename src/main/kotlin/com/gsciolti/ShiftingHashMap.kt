package com.gsciolti

open class ShiftingHashMap<K : Any, V : Any>(
    firstPair: Pair<K, V>,
    vararg otherPairs: Pair<K, V>,
) : ShiftingMap<K, V> {

    override operator fun get(key: K): V? =
        map[key]?.value

    override fun first(): V = first.value

    override fun last(): V = last.value

    override fun shiftBackward() {
        first = first.previous
        last = last.previous

        map.entries.forEach {
            map[it.key] = it.value.previous
        }
    }

    override fun values(): Collection<V> =
        map.values.map { it.value }

    private val map = hashMapOf<K, Node<V>>()
    private var first = Node(firstPair.second)
    private var last = first

    init {
        first.previous = first
        first.next = first

        map[firstPair.first] = first

        otherPairs.forEach {
            val newNode = Node(it.second)
            newNode.previous = last
            newNode.next = first
            last.next = newNode
            first.previous = newNode
            last = newNode

            map[it.first] = newNode
        }
    }

    private class Node<V : Any>(var value: V) {
        lateinit var previous: Node<V>
        lateinit var next: Node<V>
    }
}
