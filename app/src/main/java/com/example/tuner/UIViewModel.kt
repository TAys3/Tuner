package com.example.tuner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * This is an object, which is sort of like a class that is initialised on startup. Feature and benefit
 * of an object is that only one instance can ever exist during runtime. This means that I don't have to
 * instantiate it anywhere in my code, and is also accessible throughout the entire codebase.
 * 
 * (I've seen kotlin objects being referred to as singletons somewhere I think. 
 * not sure how similar they are tho)
 * 
 * Using, this, I can have the ViewModel watch this object for any changes, enabling the UI to be
 * updated when I calculate the values.
 *
 * The 'on' value isn't used as it is for a planned feature. Too scared to remove it in fear of breaking something
 */
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

/**
 * This is a ViewModel. It watches the TunerUIState object for any changes. When it does change, the
 * ViewModel changes as well. These changes are then observed by any composable functions that have
 * this as its ViewModel.
 *
 * Usually, all the data is stored in something called a data class, which is initialised within
 * the ViewModel. However, this approach does not allow me to update the UI. The way I have designed
 * the app does not allow this to happen as the calculations are happening concurrently, and are not
 * triggered by the UI. If they were, I would be able to update the values in the ViewModel, which would
 * update the UI. This is the reason for the use of the object.
 *
 * The reason why ViewModel updates are pretty much locked to being UI triggered (thru buttons and such)
 * is that the ViewModel is a class, that is initialised within the highest level composable function.
 * As such, the data that the UI watches is locked to that instance, and is not changeable by any function
 * that is not directly called from or within the highest composable function. Using an object gets around
 * this as it is directly changeable from anywhere in the codebase (could probably be an issue with a larger codebase, but who cares).
 */
class MyViewModel : ViewModel() {
    private val _tunerState = MutableStateFlow(TunerUIState)
    val tunerState: StateFlow<TunerUIState> = _tunerState.asStateFlow()
}
