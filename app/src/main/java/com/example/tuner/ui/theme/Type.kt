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

val NovaRound = FontFamily(
    Font(R.font.nova_round_regular)
)

val MPlusRounded = FontFamily(
    Font(R.font.m_plus_rounded_regular)
)

val VarelaRound = FontFamily(
    Font(R.font.varela_round_regular)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleSmall = TextStyle(
        fontFamily = VarelaRound,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
        color = Grey600
    ),
    bodySmall = TextStyle(
        fontFamily = VarelaRound,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = 20.sp,
        color = Grey600
    ),
    displayLarge = TextStyle(
        fontFamily = MPlusRounded,
        fontWeight = FontWeight.Normal,
        fontSize = 200.sp,
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)