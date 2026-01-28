package com.abn.amro.core.ui.helper

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Updates the System Status Bar icons (Clock, Battery, etc.) to be either Black (Dark) or White (Light).
 */
@Composable
fun UpdateStatusBarIcons(
    backgroundColor: Color? = null,
    allowDarkItems: Boolean? = null
) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            val isLightBackground = backgroundColor?.let { it.luminance() > 0.6f } ?: false
            val areIconsDark = allowDarkItems ?: isLightBackground

            insetsController.isAppearanceLightStatusBars = areIconsDark
        }
    }
}