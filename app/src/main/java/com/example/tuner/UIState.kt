package com.example.tuner

data class UIState (
    val pitch: String = "",
    val octave: Int = 0,
    val accuracy:Double = 0.0,
    val tuned: Boolean = false,
    val page: String = "Chromatic",
    val on: Boolean = false
) {


}