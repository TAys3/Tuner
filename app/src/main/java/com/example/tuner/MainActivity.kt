package com.example.tuner

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import com.example.tuner.ui.theme.TunerTheme

class MainActivity : ComponentActivity() {

//    val MIC_RQ = 100

    val requestMicrophonePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // MICROPHONE PERMISSION GRANTED
        } else {
            // MICROPHONE PERMISSION NOT GRANTED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        checkForPermissions(android.Manifest.permission.RECORD_AUDIO, "microphone", MIC_RQ)
        setContent {
            TunerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

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

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        fun innerCheck (name: String) {
//            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT).show()
//            }
//            when(requestCode) {
//                MIC_RQ -> innerCheck("microphone")
//            }
//        }
//    }
//    private fun checkForPermissions(permission: String, name: String, requestCode: Int) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            when {
//                ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
//                    Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT).show()
//                }
//                shouldShowRequestPermissionRationale(permission) -> showDialog(permission, name, requestCode)
//
//                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
//            }
//        }
//
//    }
//
//    private fun showDialog(permission: String, name: String, requestCode: Int) {
//        val builder = AlertDialog.Builder(this)
//
//        builder.apply {
//            setMessage("Permission to access your $name is required to use this app")
//            setTitle("Permission required")
//            setPositiveButton("Ok") { dialog, which ->
//                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
//            }
//        }
//        val dialog = builder.create()
//        dialog.show()
//
//    }
}
    private fun processPitch(pitchInHz: Float) {
        //Process the pitch here
        println(pitchInHz)
        pitch = pitchInHz.toDouble()
    }



var pitch = 0.0

@Composable
fun MainWindow(pitch: Double) {
//    Column {
    Text(text = "$pitch")
//        PitchLetter()                                                                                            //Will also be text, but a lot smaller, might combine them if I can (with them each being different sizes)
//        PitchAccuracy()
//        ReferencePitch()

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