package com.gsciolti.transactionsapi.repository.shiftingmap

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.lang.Thread.sleep
import java.time.Duration.ofMillis

class ShiftingHashMapTest {

    @Test
    fun `should allow to get elements`() {
        val map = ShiftingHashMap(1 to "one", 2 to "two")

        assertEquals("one", map[1]!!)
        assertEquals("two", map[2]!!)
        assertNull(map[3])
    }

    @Test
    fun `should allow to get first and last elements`() {
        val map = ShiftingHashMap(1 to "one", 2 to "two")

        assertEquals("one", map.first())
        assertEquals("two", map.last())
    }

    @Test
    fun `should shift elements`() {
        val map = ShiftingHashMap(
            1 to "one",
            2 to "two",
            3 to "three"
        )

        map.shiftBackward()

        assertEquals("three", map[1]!!)
        assertEquals("one", map[2]!!)
        assertEquals("two", map[3]!!)
        assertEquals("three", map.first())
        assertEquals("two", map.last())
    }

    @Test
    fun `should be able to self shift at a given pace`() {
        val map =
            ShiftingHashMap(
                1 to "one",
                2 to "two",
                3 to "three"
            ).shiftingEvery(ofMillis(300L))

        sleep(350L)
        assertEquals("three", map[1]!!)
        assertEquals("one", map[2]!!)
        assertEquals("two", map[3]!!)

        sleep(300L)
        assertEquals("two", map[1]!!)
        assertEquals("three", map[2]!!)
        assertEquals("one", map[3]!!)

        sleep(300L)
        assertEquals("one", map[1]!!)
        assertEquals("two", map[2]!!)
        assertEquals("three", map[3]!!)
    }
}