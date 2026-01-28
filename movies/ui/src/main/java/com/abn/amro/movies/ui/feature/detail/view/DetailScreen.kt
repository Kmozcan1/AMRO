package com.abn.amro.movies.ui.feature.detail.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abn.amro.core.common.model.AmroError
import com.abn.amro.core.ui.component.LoadingView
import com.abn.amro.movies.ui.components.MovieErrorView
import com.abn.amro.movies.ui.feature.detail.presentation.DetailUiState

@Composable
fun DetailScreen(
    state: DetailUiState,
    initialColor: Int,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onImdbClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        DetailStateContent(state, initialColor, onRetry, onImdbClick)

        NavigationOverlay(onBackClick)
    }
}

@Composable
private fun DetailStateContent(
    state: DetailUiState,
    initialColor: Int,
    onRetry: () -> Unit,
    onImdbClick: () -> Unit
) {
    when (state) {
        is DetailUiState.Loading -> {
            if (initialColor != 0) {
                AtmosphereOverlay(Color(initialColor), alpha = 1f)
            }
            LoadingView(tint = Color(initialColor))
        }

        is DetailUiState.Success -> {
            DetailContent(
                movie = state.movie,
                initialColor = initialColor,
                onImdbClick = onImdbClick
            )
        }

        is DetailUiState.Error -> {
            MovieErrorView(state.error, onRetry)
        }
    }
}

@Composable
fun AtmosphereOverlay(color: Color, alpha: Float) {
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.65f)
            .background(
                Brush.verticalGradient(
                    0.0f to color.copy(alpha = alpha),
                    0.25f to color.copy(alpha = alpha * 0.5f),
                    1.0f to Color.Transparent
                )
            )
    )
}

@Composable
private fun NavigationOverlay(onBackClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                )
            )
    )

    IconButton(
        onClick = onBackClick,
        modifier = Modifier
            .statusBarsPadding()
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
        )
    }
}

@Preview(name = "Loading Transition")
@Composable
private fun PreviewDetailLoading() {
    DetailScreen(
        state = DetailUiState.Loading,
        initialColor = android.graphics.Color.CYAN,
        onBackClick = {},
        onRetry = {},
        onImdbClick = {}
    )
}

@Preview(name = "Error State")
@Composable
private fun PreviewDetailError() {
    DetailScreen(
        state = DetailUiState.Error(AmroError.Network),
        initialColor = 0,
        onBackClick = {},
        onRetry = {},
        onImdbClick = {}
    )
}