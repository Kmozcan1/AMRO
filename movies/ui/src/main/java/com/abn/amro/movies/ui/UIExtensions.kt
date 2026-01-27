package com.abn.amro.movies.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abn.amro.core.common.model.AmroError
import com.abn.amro.movies.ui.feature.top100.presentation.Top100UiError
import java.util.Locale

fun Double.formatRating(): String {
    return String.format(Locale.getDefault(), "%.1f", this)
}

@Composable
fun Top100UiError.asUiText(): String {
    return when (this) {
        is Top100UiError.Empty -> stringResource(R.string.error_no_movies_found)
        is Top100UiError.Common -> when (this.error) {
            is AmroError.Network -> stringResource(R.string.error_network)
            is AmroError.Server -> stringResource(R.string.error_server)
            is AmroError.Unknown -> stringResource(R.string.error_generic)
        }
    }
}