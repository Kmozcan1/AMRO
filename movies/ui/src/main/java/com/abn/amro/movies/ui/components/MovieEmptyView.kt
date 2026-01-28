package com.abn.amro.movies.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.abn.amro.core.ui.component.ErrorView
import com.abn.amro.movies.ui.R

@Composable
fun MovieEmptyView(modifier: Modifier = Modifier, tint: Color) {
    ErrorView(
        modifier = modifier,
        message = stringResource(R.string.error_no_movies_found),
        buttonText = stringResource(R.string.refresh_button),
        icon = Icons.Default.SearchOff,
        tint = tint,
        hasButton = false
    )
}