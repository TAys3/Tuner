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
import com.example.tuner.ui.theme.TunerTheme

class MainActivity : ComponentActivity() {

    val requestMicrophonePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
//            Toast.makeText(applicationContext, "mic permission granted", Toast.LENGTH_SHORT).show()
        } else {
//            Toast.makeText(applicationContext, "mic permission refused", Toast.LENGTH_SHORT).show()
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
        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)
        val pdh = PitchDetectionHandler { res, e ->
            val pitchInHz = res.pitch
            runOnUiThread {
                processPitch(pitchInHz)
            }
        }

        val pitchProcessor =
            PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050F, 1024, pdh)
        dispatcher.addAudioProcessor(pitchProcessor)

        val audioThread = Thread(dispatcher, "Audio Thread")
        audioThread.start()
    }

    private fun requestMicrophonePermission() {
        requestMicrophonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
}




private fun processPitch(pitchInHz: Float) {
    //Process the pitch here
    println(pitchInHz)
    pitch = pitchInHz.toDouble()
}

var pitch = 0.0

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