package com.abn.amro.core.ui.helper

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun UpdateStatusBarIcons(backgroundColor: Color? = null, forceLightColor: Boolean? = null) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val isLightBackground =
                backgroundColor?.let { it.luminance() > 0.6f } == true || forceLightColor == false
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                isLightBackground
        }
    }
}