package com.example.tuner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tuner.R

/**
 * A few of the fonts used in my app. Some are not used anymore, but are still here cause might use them later.
 */
val NovaRound = FontFamily(
    Font(R.font.nova_round_regular)
)

val MPlusRounded = FontFamily(
    Font(R.font.m_plus_rounded_regular)
)

val VarelaRound = FontFamily(
    Font(R.font.varela_round_regular)
)

val IBMMedium = FontFamily(
    Font(R.font.ibm_plex_mono_medium)
)

val IBMReg = FontFamily(
    Font(R.font.ibm_plex_mono_regular)
)

val IBMLight = FontFamily(
    Font(R.font.ibm_plex_mono_light)
)

/**
 * These are here for reused typography styles.
 */
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleSmall = TextStyle(
        fontFamily = IBMReg,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
        color = Grey600
    ),
    bodySmall = TextStyle(
        fontFamily = IBMMedium,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = 20.sp,
        color = Grey600
    ),
    displayLarge = TextStyle(
        fontFamily = IBMLight,
        fontWeight = FontWeight.Normal,
        fontSize = 200.sp,
    )
)