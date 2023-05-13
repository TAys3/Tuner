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


// Old/Unused Tests. Keeping for proof of progress and reference

//    @Test
//    fun mapFunTesting(){
//        assertEquals("E", pitchListSharps[7])
//    }

//    @Test
//    fun closestPitchTest() {
//        assertArrayEquals(arrayOf("A", "0", "0.0"), closestPitch(0.0, pitchListSharps))
//        assertArrayEquals(arrayOf("A", "1", "0.0"), closestPitch(-12.0, pitchListSharps))
//        assertArrayEquals(arrayOf("A", "1", "0.0"), closestPitch(12.0, pitchListSharps))

//    @Test
//    fun timeTest() {
//        val time1 = measureTimeMillis { closestPitchTest() }
//        val time2 = measureTimeMillis { pitchWhenTest() }
//
//        println("Time 1: $time1, Time 2: $time2")
//    }