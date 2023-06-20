package com.example.tuner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable function for the app's settings screen (no features yet)
 */
@Composable
fun SettingsScreen() {
    Column(
        verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.size(48.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Coming soon (maybe)",
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Spacer(modifier = Modifier.size(48.dp))
    }

}