package com.example.tuner

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.example.tuner.ui.theme.Grey900

/**
 * Composable function for the app's title bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Title_bar(page: String, icon: ImageVector, navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = { Text(text = page, style = MaterialTheme.typography.titleSmall) },
        navigationIcon = {
            IconButton(onClick = {
                if (page == "Chromatic") navController.navigate(
                    TunerScreen.Settings.name
                ) else navController.navigateUp()
            }
            ) {
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
 *
 * (not currently in use and never actually developed.
 * This code here is example code I was going to build from into what I wanted. Keeping it just in case)
 */
@Composable
fun Navbar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Tunings", "Chromatic", "Metronome")

    NavigationBar(containerColor = Grey900) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index })
        }
    }
}