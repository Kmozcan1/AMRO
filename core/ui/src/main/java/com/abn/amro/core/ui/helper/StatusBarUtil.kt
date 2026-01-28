package com.abn.amro.core.ui.helper

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Updates the System Status Bar icons (Clock, Battery, etc.) to be either Black (Dark) or White (Light).
 */
@Composable
fun UpdateStatusBarIconsToWhite() {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
        }
    }
}