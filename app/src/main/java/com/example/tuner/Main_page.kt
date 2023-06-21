package com.example.tuner

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tuner.ui.theme.Green200
import com.example.tuner.ui.theme.IBMLight
import com.example.tuner.ui.theme.IBMMedium
import kotlin.math.abs

/**
 * Composable function that contains all of the content of the chromatic page
 */
@Composable
fun ChromaticMain(
    sharp: Boolean,
    pitch: String,
    octave: String,
    accuracy: String,
    tuned: Double
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(4.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.Top)) {
                Spacer(modifier = Modifier.height(58.dp))
                Text(
                    text = "#",
                    fontFamily = IBMMedium,
                    fontSize = 40.sp,
                    color = if (!sharp) Color(0xFF343333) else {
                        if (tuned >= 1.0) Color(0xFF95EE9E) else Color(0xFFC0BCBC)       //Decides the colour of the text. Kinda ugly, but it works and is readable (at least to me)
                    }
                )
            }
            Text(
                text = pitch,
                style = MaterialTheme.typography.displayLarge,
                color = if (tuned >= 1.0) Color(0xFF95EE9E) else Color(0xFFC0BCBC),      //This one is nicer
                modifier = Modifier.align(Alignment.Bottom)
            )
            Column(modifier = Modifier.align(Alignment.Bottom)) {
                Text(
                    text = octave,
                    fontFamily = IBMLight,
                    fontSize = 40.sp,
                    color = if (tuned >= 1.0) Color(0xFF95EE9E) else Color(0xFFC0BCBC)   //Same thing
                )
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            BarGraph(accuracy.toInt())
            Spacer(
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth()
            )
            Text(
                text = when (accuracy.toInt()) {in -100..0 -> accuracy else -> "+$accuracy"}, //Same purpose. Did this cause its shorter than the equivalent nested inline if statement. Ngl, kinda proud of this one.
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
                .height(30.dp)
                .fillMaxWidth()
        )
        CircularProgressIndicator(
            progress = tuned.toFloat(),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Green200
        )
    }
}

/**
 * A composable function that displays a 'graph' to visually represent the accuracy
 */
@Composable
fun BarGraph(accuracy: Int) {
    //Calculating the offset of the little bar, which is how far from the center it is
    val offset: Int = if (abs(accuracy) > 45) {
        if (accuracy > 0) 45 else -45
    } else {
        accuracy
    }
    Box {
        Image(
            painter = painterResource(id = R.drawable.bar1),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(id = R.drawable.bar2),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .offset { IntOffset(offset * 10, 0) }
        )
    }

}