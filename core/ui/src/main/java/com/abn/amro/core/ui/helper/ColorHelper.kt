package com.abn.amro.core.ui.helper

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ColorHelper {
    // Applies black alpha on a color so that the white text looks better.
    suspend fun extractCinematicColor(bitmap: Bitmap, isDarkMode: Boolean): Color? =
        withContext(Dispatchers.Default) {
            val palette = Palette.from(bitmap).generate()
            val swatch = if (isDarkMode) {
                palette.dominantSwatch ?: palette.vibrantSwatch ?: palette.mutedSwatch
            } else {
                palette.mutedSwatch ?: palette.dominantSwatch ?: palette.mutedSwatch
            }

            swatch?.let {
                val rawColor = Color(it.rgb)

                if (isDarkMode) {
                    if (rawColor.luminance() > 0.2f) {
                        Color.Black.copy(alpha = 0.5f).compositeOver(rawColor)
                    } else {
                        rawColor
                    }
                } else {
                    if (rawColor.luminance() > 0.4f) {
                        Color.Black.copy(alpha = 0.3f).compositeOver(rawColor)
                    } else {
                        rawColor
                    }
                }
            }
        }
}