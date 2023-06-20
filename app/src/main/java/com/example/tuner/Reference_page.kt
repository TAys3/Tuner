package com.example.tuner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * A composable function that shows the reference pitch, and buttons and a slider to update it
 */
@Composable
fun RefPitch(refpitch: String) {
    var sliderPosition by remember { mutableStateOf(440F) }
    Column(
        verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { TunerUIState.refPitch -= 1; sliderPosition -= 1 }) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = "Decrease"
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = refpitch,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 50.sp
                )
                Text(text = "Hz", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(modifier = Modifier.size(16.dp))
            IconButton(onClick = { TunerUIState.refPitch += 1; sliderPosition += 1 }) {
                Icon(
                    Icons.Rounded.ArrowForward,
                    contentDescription = "Increase"
                )
            }
        }
        Column {
            Slider(
                modifier = Modifier
                    .semantics { contentDescription = "Reference Pitch Slider" }
                    .padding(16.dp),
                value = sliderPosition,
                onValueChange = { sliderPosition = it; TunerUIState.refPitch = sliderPosition.roundToInt() },
                valueRange = 400f..500f,
                onValueChangeFinished = {
                    TunerUIState.refPitch = sliderPosition.roundToInt()
                }
            )
        }
    }
}