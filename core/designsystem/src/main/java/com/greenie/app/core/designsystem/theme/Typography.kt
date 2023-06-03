package com.greenie.app.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.greenie.app.core.designsystem.R


private val primaryFont = FontFamily(
    Font(R.font.notosans_extrabold, weight = FontWeight.ExtraBold),
    Font(R.font.notosans_bold, weight = FontWeight.Bold),
    Font(R.font.notosans_medium, weight = FontWeight.Medium),
    Font(R.font.notosans_regular, weight = FontWeight.Normal),
    Font(R.font.notosans_light, weight = FontWeight.Light),
    Font(R.font.notosans_thin, weight = FontWeight.Thin),
)

internal val primaryTextStyle = TextStyle(
    fontFamily = primaryFont,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 18.sp,
    color = Colors.headline,
    platformStyle = PlatformTextStyle(
        includeFontPadding = false,
    ),
)

object GreenieTypography {
    val head = TextStyle(
        fontFamily = primaryFont,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
    val sub_head = TextStyle(
        fontFamily = primaryFont,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
    val b_head = TextStyle(
        fontFamily = primaryFont,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
    val body = TextStyle(
        fontFamily = primaryFont,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
    val cta = TextStyle(
        fontFamily = primaryFont,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
    val b_body = TextStyle(
        fontFamily = primaryFont,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
    val b_head_reg = TextStyle(
        fontFamily = primaryFont,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
    val number = TextStyle(
        fontFamily = primaryFont,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
    val a_result = TextStyle(
        fontFamily = primaryFont,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
    val banner_head = TextStyle(
        fontFamily = primaryFont,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
    val number_60 = TextStyle(
        fontFamily = primaryFont,
        fontSize = 60.sp,
        fontWeight = FontWeight.Medium,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )
}

internal val primaryTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = primaryFont,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
)