package com.example.tuner

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.AudioProcessor
import com.example.tuner.ui.theme.TunerTheme
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestMicrophonePermission()
        setContent {
            TunerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainWindow(pitch)
                }
            }
        }

        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(32000, 2048, 1024)
        val pdh = PitchDetectionHandler { res, e ->
            val pitchInHz = res.pitch
            runOnUiThread {
                processPitch(pitchInHz)
            }
        }

        val movingAverageFilter = MovingAverageFilter(windowSize = 5)
        dispatcher.addAudioProcessor(movingAverageFilter)

        val pitchProcessor =
            PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 32000F, 2048, pdh)
        dispatcher.addAudioProcessor(pitchProcessor)

        val audioThread = Thread(dispatcher, "Audio Thread")
        audioThread.start()
    }

    private fun requestMicrophonePermission() {
        requestMicrophonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
}


class MovingAverageFilter(private val windowSize: Int) : AudioProcessor {
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

    override fun processingFinished() {}
}


private fun processPitch(pitchInHz: Float) {
    if (pitchInHz != -1.0F) {
        var semitones = numSemitones(pitchInHz.toDouble(), refPitch)
        var values = closestPitchWhen(semitones)
        println("Freq: $pitchInHz Pitch: ${values[0]}, Octave: ${values[1]}, Acc: ${values[2]}")
    }
}

fun numSemitones(pitch: Double, reference: Int): Double {
    return 12 * log(pitch / reference, 2.0)
}


fun closestPitchWhen(semitones: Double): Array<String> {
    var rounded = semitones.roundToInt()
    var counter = 0
    while (rounded < -11 || rounded > 11) {
        if (rounded < -11) {
            rounded += 12
        }
        if (rounded > 11){
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
    if(semitones < 0) {
        octave -= counter
    } else {
        octave += counter
    }
    
    return arrayOf(pitchLetter, "$octave", "$accuracy")
}

var pitch = 0.0
var refPitch = 440


@Composable
fun MainWindow(pitch: Double) {
    Text(text = "$pitch")
}

@Composable
fun PitchLetter() {

}

@Composable
fun PitchAccuracy() {

}

@Composable
fun ReferencePitch() {

}


@Preview(showBackground = true)
@Composable
fun ManWindowPreview() {
    TunerTheme {
        MainWindow(pitch)
    }
}