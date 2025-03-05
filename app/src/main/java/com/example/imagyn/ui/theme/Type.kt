package com.example.imagyn.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import com.example.imagyn.R




val displayFontFamily = FontFamily(
    Font(R.font.itim_regular)
)

val baseline = Typography()
val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
//    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
//    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
//    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
//    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
//    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
//    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
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