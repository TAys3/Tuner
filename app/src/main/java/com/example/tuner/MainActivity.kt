package com.example.tuner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tuner.ui.theme.TunerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}

@Composable
fun MainWindow() {
    val cents = 0
    val referencePitch = 440
    Column() {
        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            Text(text = "#/b", Modifier.padding(8.dp))                                              //Will be an image that changes colour and sharp/flat
            Text(text = "A", Modifier.padding(8.dp))                                                //Will just be a large letter in a text element
            Text(text = "1", Modifier.padding(8.dp))                                                //Will also be text, but a lot smaller, might combine them if I can (with them each being different sizes)
        }
        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            Text(text = "Bar goes here")                                                            //Will be an image with a rectangle on top?
            //Marker goes here
        }
        Text(text = "$cents cents", modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            Button(onClick = { /*TODO*/ }) {
//                Image(painter = painterResource(id = {}), contentDescription = null)
                Text(text = "-")
            }
            Text(text = "$referencePitch")                                                          //Large text
            Text(text = "Hz")                                                                       //Smaller text
            Button(onClick = { /*TODO*/ }) {
//                Image(painter = painterResource(id = {}), contentDescription = null)
                Text(text = "+")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TunerTheme {
        MainWindow()
    }
}