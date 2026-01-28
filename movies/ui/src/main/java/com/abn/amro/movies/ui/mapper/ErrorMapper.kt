package com.abn.amro.movies.ui.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abn.amro.core.common.model.AmroError
import com.abn.amro.movies.ui.R

@Composable
fun AmroError.toUserMessage(): String {
    return when (this) {
        AmroError.Network -> stringResource(R.string.error_network)
        AmroError.Server -> stringResource(R.string.error_server)
        is AmroError.Unknown -> stringResource(R.string.error_generic)
    }
}