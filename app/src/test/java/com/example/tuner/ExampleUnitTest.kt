package com.example.tuner

import org.junit.Test

import org.junit.Assert.*
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun semitoneEstimations() {
        assertEquals(0, (numSemitones(440.0, 440)).toInt())
        assertEquals(-12, (numSemitones(220.0, 440)).toInt())
    }

    @Test
    fun pitchWhenTest() {
        assertArrayEquals(arrayOf("A", "0", "0.0"), closestPitchWhen(0.0))
        assertArrayEquals(arrayOf("A", "1", "0.0"), closestPitchWhen(12.0))
        assertArrayEquals(arrayOf("A", "1", "0.0"), closestPitchWhen(-12.0))
    }

}
