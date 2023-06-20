package com.example.tuner

/**
 * Imports. Quite a lot of them
 */
import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.filters.HighPass
import be.tarsos.dsp.filters.LowPassFS
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import com.example.tuner.ui.theme.IBMLight
import com.example.tuner.ui.theme.IBMMedium
import com.example.tuner.ui.theme.NovaRound
import com.example.tuner.ui.theme.TunerTheme
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.roundToInt


/**
 * Where it all begins
 */
class MainActivity : ComponentActivity() {

    /**
     * This is part of checking and asking for mic permission
     */
    private val requestMicrophonePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(applicationContext, "Mic permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Mic permission refused", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Boilerplate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestMicrophonePermission()
        setContent {
            TunerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainWindow()
                }
            }
        }

        /**
         * [From](https://stackoverflow.com/questions/31231813/tarsosdsp-pitch-analysis-for-dummies)
         *
         * Incoming audio stream from mic is read and chopped into frames. AudioDispatcher is responsible for this
         *
         * AudioDispatcher also wraps an audio frame into an AudioEvent object. This is then sent through a chain of AudioProcessors
         *
         * Values in fromDefaultMicrophone() are the sample rate, audio buffer size and buffer overlap
         */
        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 4096, 1024)
        val pdh = PitchDetectionHandler { res, e ->            //Res is the pitch detection result, e is the Audio Event (not used)
            val pitchInHz = res.pitch                   //The calculated fundamental pitch
            val probability = res.probability           //The probability that the calculated pitch is correct
            val pitched = res.isPitched               //Whether or not the algorithm thinks the audio is 'pitched'. As in, a sound/note is being played
            runOnUiThread {
                processPitch(pitchInHz, probability, pitched)
            }
        }

        // Adding filters to the AudioProcessor chain in order to increase accuracy
        val lowPassFilter = LowPassFS(4000F, 44100F)
        dispatcher.addAudioProcessor(lowPassFilter)

        val highPassFiler = HighPass(50F, 44100F)
        dispatcher.addAudioProcessor(highPassFiler)

        val movingAverageFilter = MovingAverageFilter(windowSize = 10)
        dispatcher.addAudioProcessor(movingAverageFilter)

        /**
         * Adding the pitch processor to the AudioProcessor chain
         *
         * It uses the YIN algorithm. Other values in PitchProcessor() are sample rate, buffer size and PitchDetectionHandler
         */
        val pitchProcessor =
            PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 44100F, 4096, pdh)
        dispatcher.addAudioProcessor(pitchProcessor)

        val audioThread = Thread(dispatcher, "Audio Thread")
        audioThread.start()
    }

    /**
     * Requests mic permission so that the app doesn't crash on launch and so that the user can give mic permission
     */
    private fun requestMicrophonePermission() {
        requestMicrophonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
}


/**
 * This is a phind creation. It is a filter used to reduce noise in the audio.
 * Added it to help with the inaccuracy problem. Seems to have an impact.
 */
class MovingAverageFilter(private val windowSize: Int) : AudioProcessor {

    /**
     * ngl, can't explain how this is supposed to work.
     * Phind describes it as so:
     *
     * The purpose of this code is to smooth out the audio signal by calculating the moving average of each element with its neighboring elements within a specified window size.
     * The process:
     * 1. The process function takes an AudioEvent as input, which contains an audio buffer.
     * 2. The function retrieves the float buffer from the AudioEvent.
     * 3. It creates a new float array called filteredBuffer with the same size as the input buffer.
     * 4. It iterates over each element in the input buffer using a nested loop.
     * 5. For each element, it calculates the sum of the previous windowSize elements (including the current element) in the input buffer.
     * 6. It divides the sum by the windowSize to calculate the average.
     * 7. It stores the average in the corresponding position of the filteredBuffer array
     * 8. Finally, the filteredBuffer is copied back to the original buffer using System.arraycopy.

     */
    override fun process(audioEvent: be.tarsos.dsp.AudioEvent?): Boolean {
        audioEvent?.let {
            val buffer = it.floatBuffer
            val filteredBuffer = FloatArray(buffer.size)

            for (i in buffer.indices) {
                var sum = 0f
                for (j in 0 until windowSize) {
                    if (i - j >= 0) {
                        sum += buffer[i - j]
                    }
                }
                filteredBuffer[i] = sum / windowSize
            }

            System.arraycopy(filteredBuffer, 0, buffer, 0, buffer.size)
        }
        return true
    }

    /**
     * This function is called when the audio processing is finished.
     * Here, you can define what should happen when the processing is complete.
     * I don't have any use for it, however, Phind created it, and I'm too afraid of messing with it.
     *
     * So it shall stay here. Could be useful in the future. Maybe
     */
    override fun processingFinished() {}
}


/**
 * This processes the pitch and updates the TunerUIState object with the values
 */
private fun processPitch(pitchInHz: Float, probability: Float, pitched: Boolean) {
    if (pitched && pitchInHz != -1.0F && probability > 0.93F) {
        val semitones = numSemitones(pitchInHz.toDouble())
        val values = closestPitchWhen(semitones)
        if ("#" in values[0]) {
            TunerUIState.pitch = values[0][0].toString()
            TunerUIState.sharp = true
        } else {
            TunerUIState.pitch = values[0]
            TunerUIState.sharp = false
        }
        TunerUIState.octave = values[1]
        TunerUIState.accuracy = values[2].toDouble().roundToInt()
        if (abs(values[2].toDouble()) < 10) {
            if (TunerUIState.tuned < 1.0) {
                TunerUIState.tuned += 0.05
            }
        } else {
            if (TunerUIState.tuned > 0.2) {
                TunerUIState.tuned -= 0.2
            } else {
                TunerUIState.tuned = 0.0
            }
        }
    }
    if (pitchInHz == -1.0F) {
        if (TunerUIState.tuned > 0.1) {
            TunerUIState.tuned -= 0.1
        } else {
            TunerUIState.tuned = 0.0
        }
    }
}

/**
 * Calculates, in semitones, how far away the pitch is from a reference pitch
 *
 * The derivation of this can be found in my log book
 */
fun numSemitones(pitch: Double): Double {
    return 12 * log(pitch / (TunerUIState.refPitch), 2.0)

    //TODO
    //Uhh so its accurate now?? No idea how it fixed itself. The problem wasn't this function, but the pitch detection from Tarsos.
    //But I was going to have to compensate for it here.
    //
    //If it goes rogue again, must do the following I guess:
    //Graph the inaccuracies and fix them.
    //Or if not bothered, just change the value of refPitch a bit to 'fix' it for standard tuning
}

/**
 * This function converts the semitones to a pitch, octave and accuracy, returning them as an array. It utilises the inbuilt when() function (a switch case/case match) to do so.
 */
fun closestPitchWhen(semitones: Double): Array<String> {
    var rounded = semitones.roundToInt()
    var counter = 0
    while (rounded < -11 || rounded > 11) {
        if (rounded < -11) {
            rounded += 12
        }
        if (rounded > 11) {
            rounded -= 12
        }
        counter += 1
    }
    val accuracy = (semitones - semitones.roundToInt()) * 100
    var pitchLetter = ""
    when (rounded) {
        0 -> pitchLetter = "A"
        1, -11 -> pitchLetter = "A#"
        2, -10 -> pitchLetter = "B"
        3, -9 -> pitchLetter = "C"
        4, -8 -> pitchLetter = "C#"
        5, -7 -> pitchLetter = "D"
        6, -6 -> pitchLetter = "D#"
        7, -5 -> pitchLetter = "E"
        8, -4 -> pitchLetter = "F"
        9, -3 -> pitchLetter = "F#"
        10, -2 -> pitchLetter = "G"
        11, -1 -> pitchLetter = "G#"
    }

    var octave = 4
    if (semitones < 0) {
        octave -= counter
    } else {
        octave += counter
    }

    return arrayOf(pitchLetter, "$octave", "$accuracy")
}

/**
 * An enum class that is used by the NavHost to define and create routes. The NavHost is what does
 * the navigation thru the app (to different pages and such).
 */
enum class TunerScreen {
    Chromatic,
    Refpitch,
    Settings
}

/**
 * The highest level composable function. It holds the ViewModel and the NavHost/NavController.
 */
@OptIn(ExperimentalMaterial3Api::class) //Since Material3 is 'experimental' I have to do this. Otherwise it yells at me.
@Composable
fun MainWindow(
    viewModel: MyViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val UiState by viewModel.tunerState.collectAsState()
    Scaffold(
        topBar = {
            Title_bar(
                page = UiState.page,
                icon = if (UiState.page == "Chromatic") Icons.Outlined.Settings else Icons.Outlined.ArrowBack,
                navController = navController
            )
        },
//        bottomBar = { Navbar() },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (TunerUIState.page == "Chromatic") {
                FloatingActionButton(onClick = { navController.navigate(TunerScreen.Refpitch.name) },
                    containerColor = MaterialTheme.colorScheme.onSurface) {
                    Text(
                        text = "Hz",
                        fontFamily = NovaRound,
                        color = MaterialTheme.colorScheme.surface,
                        fontSize = 20.sp
                    )
                }
            }
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = TunerScreen.Chromatic.name,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(route = TunerScreen.Chromatic.name) {
                ChromaticMain(
                    sharp = UiState.sharp,
                    pitch = UiState.pitch,
                    octave = UiState.octave,
                    accuracy = UiState.accuracy.toString(),
                    tuned = UiState.tuned
                )
                TunerUIState.page = "Chromatic"
            }
            composable(route = TunerScreen.Refpitch.name) {
                RefPitch(UiState.refPitch.toString())
                TunerUIState.page = "Reference"
            }
            composable(route = TunerScreen.Settings.name) {
                SettingsScreen()
                TunerUIState.page = "Settings"
            }
        }
    }
}


/**
 * These following functions are for previews of the UI while developing the app. These previews can be viewed by pressing the split or design button in the top right
 */
@Preview(showBackground = true)
@Composable
fun MainWindowPreview() {
    TunerTheme(darkTheme = true) {
        MainWindow()
    }
}

@Preview(showBackground = true)
@Composable
fun ChromaticPreview() {
    TunerTheme(darkTheme = true) {
        ChromaticMain(
            sharp = false,
            pitch = "A",
            octave = "4",
            accuracy = "0",
            tuned = 0.0
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RefWindowPrev() {
    TunerTheme(darkTheme = true) {
        RefPitch(refpitch = TunerUIState.refPitch.toString())
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPrev() {
    TunerTheme(darkTheme = true) {
        SettingsScreen()
    }
}