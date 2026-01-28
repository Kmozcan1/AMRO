package com.abn.amro.movies.ui.feature.detail.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abn.amro.movies.ui.feature.detail.presentation.DetailUiEffect
import com.abn.amro.movies.ui.feature.detail.presentation.DetailUiEvent
import com.abn.amro.movies.ui.feature.detail.presentation.DetailViewModel
import com.abn.amro.movies.ui.feature.detail.view.DetailScreen

@Composable
fun DetailRoute(
    initialColor: Int,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                DetailUiEffect.NavigateBack -> onNavigateBack()
                is DetailUiEffect.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                    context.startActivity(intent)
                }
            }
        }
    }

    DetailScreen(
        state = state,
        initialColor = initialColor,
        onBackClick = { viewModel.onEvent(DetailUiEvent.OnBackClicked) },
        onRetry = { viewModel.onEvent(DetailUiEvent.OnRetry) },
        onImdbClick = { viewModel.onEvent(DetailUiEvent.OnImdbClicked) }
    )
}