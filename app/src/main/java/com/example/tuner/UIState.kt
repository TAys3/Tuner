package com.example.tuner

data class UIState (
    val pitch: String = "",
    val sharp: Boolean = false,
    val octave: Int = 0,
    val accuracy:Double = 0.0,
    val tuned: Double = 0.0,
    val page: String = "Chromatic",
    val on: Boolean = false
) {


}