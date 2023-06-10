package com.example.tuner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TunerUIState {
    var pitch: String by mutableStateOf("A")
    var sharp: Boolean by mutableStateOf(false)
    var octave: String by mutableStateOf("4")
    var accuracy: Int by mutableStateOf(0)
    var tuned: Double by mutableStateOf(0.0)
    var page: String by mutableStateOf("Chromatic")
    var on: Boolean by mutableStateOf(false)
    var refPitch: Int by mutableStateOf(440)
}

class MyViewModel : ViewModel() {
    private val _tunerState = MutableStateFlow(TunerUIState)
    val tunerState: StateFlow<TunerUIState> = _tunerState.asStateFlow()

    fun setPitch(Pitch: String) {
        tunerState.value.pitch = Pitch
    }
}
