package com.abn.amro.core.ui.extension

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun Color.contentColor(): Color {
    // Calculate brightness of the background color
    // 0.0 is black, 1.0 is white
    val luminance = ColorUtils.calculateLuminance(this.toArgb())

    // If background is dark (< 0.6), use White text. Else use Black.
    return if (luminance < 0.6) Color.White else Color.Black
}