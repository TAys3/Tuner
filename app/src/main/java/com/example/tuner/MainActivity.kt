package com.example.tuner

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import com.example.tuner.ui.theme.IBMLight
import com.example.tuner.ui.theme.IBMMedium
import com.example.tuner.ui.theme.NovaRound
import com.example.tuner.ui.theme.TunerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.log
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    val requestMicrophonePermissionLauncher = registerForActivityResult(
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
        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(32000, 2048, 1024)
        val pdh = PitchDetectionHandler { res, e ->
            val pitchInHz = res.pitch
            runOnUiThread {
                processPitch(pitchInHz)
            }
        }

        /**
         * Adding the noise filter to the AudioProcessor chain
         */
        val movingAverageFilter = MovingAverageFilter(windowSize = 5)
        dispatcher.addAudioProcessor(movingAverageFilter)

        /**
         * Adding the pitch processor to the AudioProcessor chain
         *
         * It uses the YIN algorithm. Other values in PitchProcessor() are sample rate and buffer size
         */
        val pitchProcessor =
            PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 32000F, 2048, pdh)
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
 * Added it to help with the inaccuracy problem. No idea if it has any impact, or actually works.
 */
class MovingAverageFilter(private val windowSize: Int) : AudioProcessor {

    /**
     * ngl, can't explain how this is supposed to work, just that it supposedly uses some simple algorithm to reduce noise.
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
     * Don't understand why it must override this function, but do nothing.
     * However, I am too afraid to mess with it. Need to look at the documentation and maybe ask phind.
     */
    override fun processingFinished() {}
}


/**
 * This processes the pitch, and currently prints the results to the log
 */
private fun processPitch(pitchInHz: Float) {
    if (pitchInHz != -1.0F) {
        var semitones = numSemitones(pitchInHz.toDouble())
        var values = closestPitchWhen(semitones)
        println("Freq: $pitchInHz Pitch: ${values[0]}, Octave: ${values[1]}, Acc: ${values[2]}")
        if("#" in values[0]) {
            TunerUIState.pitch = values[0][0].toString()
            TunerUIState.sharp = true
        } else {
            TunerUIState.pitch = values[0]
        }
        TunerUIState.octave = values[1]
        TunerUIState.accuracy = values[2].toDouble().roundToInt()
    }
}

/**
 * Calculates, in semitones, how far away the pitch is from a reference pitch
 *
 * The derivation of this can be found in my log book
 */
fun numSemitones(pitch: Double): Double {
    return 12 * log(pitch / TunerUIState.refPitch, 2.0)
}

/**
 * This function converts the semitones to a pitch, octave and accuracy, returning them as an array. It utilises the inbuilt when() function to do so.
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
    var accuracy = (semitones - semitones.roundToInt()) * 100
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

var refPitch = 440


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWindow(viewModel: MyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val UiState by viewModel.tunerState.collectAsState()
    Scaffold(
        topBar = {
            Title_bar(
                page = UiState.page,
                icon = Icons.Outlined.Settings
            ) /* TODO */  /* Add the ability to change pages and the associated variables */
        },
        bottomBar = { Navbar() },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ } /*TODO add the elevation*/,
                containerColor = MaterialTheme.colorScheme.onSurface) {
                Text(
                    text = "Hz",
                    fontFamily = NovaRound,
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = 20.sp
                )
            }
        }
    ) { contentPadding ->
        ContentStuff(
            contentPadding,
            pitch = UiState.pitch,
            octave = UiState.octave,
            accuracy = UiState.accuracy.toString(),
            sharp = UiState.sharp
        )/* TODO */ // Learn this shit cause I need to somehow put the other UI elements here
    }
}

/**
 * Composable function for the app's title bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Title_bar(page: String, icon: ImageVector) {
    CenterAlignedTopAppBar(
        title = { Text(text = page, style = MaterialTheme.typography.titleSmall) },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    )
}

/**
 * Composable function for the app's navigation bar
 */
@Composable
fun Navbar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Tunings", "Chromatic", "Metronome")

    //TODO
    //Fix up icons and text. Maybe remove text
    NavigationBar(containerColor = com.example.tuner.ui.theme.Grey900) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index })
        }
    }
}


/**
 * Composable function that contains all of the main body content of the chromatic page
 */
@Composable
fun ContentStuff(
    paddingValues: PaddingValues,
    sharp: Boolean,
    pitch: String,
    octave: String,
    accuracy: String
) {
    var colours: Array<Color> = arrayOf(Color(0xFF343333), Color(0xFF343333))
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.Top)) {
                Spacer(modifier = Modifier.height(58.dp))
                Text(
                    text = "#",
                    fontFamily = IBMMedium,
                    fontSize = 40.sp,
                    color = Color(0xFF343333)
                )
            }
            Text(
                text = pitch,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.align(Alignment.Bottom)
            )
            Column(modifier = Modifier.align(Alignment.Bottom)) {
                Text(
                    text = octave,
                    fontFamily = IBMLight,
                    fontSize = 40.sp,
                )
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
        Spacer(
            modifier = Modifier
                .height(16.dp)
                .fillMaxWidth()
        )
        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            BarGraph()
            Text(
                text = accuracy,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "cents",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
        )
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = com.example.tuner.ui.theme.Green200
        ) /*TODO make it determinate or replace it with linear that is shaped*/
    }
}

/**
 * A composable function that displays and updates a graph that visually represents the accuracy
 */
@Composable
fun BarGraph() {
    //TODO replace this image with something bar-like that moves
    Image(
        painter = painterResource(id = R.drawable.samsung_galaxy_s10__22),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * A composable function that is a new window for updating the reference pitch
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefPitch() {
    Scaffold(
        topBar = {
            Title_bar(
                page = "Reference",
                icon = Icons.Outlined.ArrowBack
            ) /* TODO */  /* Add the ability to change pages and the associated variables */
        },
        bottomBar = { Navbar() }
    ) {
        Column(
            verticalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Decrease"
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "440",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 50.sp
                    ) /*TODO change to a text field (maybe)*/
                    Text(text = "Hz", style = MaterialTheme.typography.titleSmall)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Rounded.ArrowForward,
                        contentDescription = "Increase"
                    )
                }
            }
            var sliderPosition by remember { mutableStateOf(440f) }
            Column {
                Slider(
                    modifier = Modifier.semantics { contentDescription = "Localized Description" },
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 400f..500f,
                    onValueChangeFinished = {
                        // TODO
                        // launch some business logic update with the state you hold
                        // viewModel.updateSelectedSliderValue(sliderPosition)
                    }
                )
            }
        }
    }
}


/**
 * These functions are for previews of the UI while developing the app. These previews can be viewed buy pressing the spit or design button in the top right
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
fun RefWindowPrev() {
    TunerTheme(darkTheme = true) {
        RefPitch()
    }
}