package com.abn.amro.movies.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.abn.amro.core.common.model.AmroError
import com.abn.amro.core.ui.component.ErrorView
import com.abn.amro.core.ui.theme.AmroTeal
import com.abn.amro.movies.ui.R
import com.abn.amro.movies.ui.mapper.toUserMessage

@Composable
fun MovieErrorView(
    modifier: Modifier = Modifier,
    error: AmroError,
    onRetry: () -> Unit,
    tint: Color = AmroTeal
) {
    val icon = when (error) {
        AmroError.Network -> Icons.Default.WifiOff
        else -> Icons.Default.Warning
    }

    ErrorView(
        modifier = modifier,
        message = error.toUserMessage(),
        buttonText = stringResource(R.string.retry_button),
        icon = icon,
        onRetry = onRetry,
        tint = tint
    )
}