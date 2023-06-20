package com.example.tuner

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun semitoneEstimations() {
        assertEquals(0, (numSemitones(440.0)).toInt())
        assertEquals(-12, (numSemitones(220.0)).toInt())
    }

    @Test
    fun pitchWhenTest() {
        assertArrayEquals(arrayOf("A", "4", "0.0"), closestPitchWhen(0.0))
        assertArrayEquals(arrayOf("A", "5", "0.0"), closestPitchWhen(12.0))
        assertArrayEquals(arrayOf("A", "3", "0.0"), closestPitchWhen(-12.0))
    }

}
