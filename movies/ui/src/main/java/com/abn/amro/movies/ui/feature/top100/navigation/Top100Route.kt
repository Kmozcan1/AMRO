package com.abn.amro.movies.ui.feature.top100.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abn.amro.movies.ui.feature.top100.presentation.Top100UiEffect
import com.abn.amro.movies.ui.feature.top100.presentation.Top100UiEvent
import com.abn.amro.movies.ui.feature.top100.presentation.Top100ViewModel
import com.abn.amro.movies.ui.feature.top100.view.Top100Screen

@Composable
fun Top100Route(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: Top100ViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Top100UiEffect.NavigateToDetail -> onNavigateToDetail(effect.movieId)
                is Top100UiEffect.ShowToast -> { }
            }
        }
    }

    Top100Screen(
        state = state,
        onRetry = { viewModel.onEvent(Top100UiEvent.OnRetry) },
        onGenreClick = { id -> viewModel.onEvent(Top100UiEvent.OnGenreSelected(id)) },
        onMovieClick = { id -> viewModel.onEvent(Top100UiEvent.OnMovieClicked(id)) },
        onSortConfigChanged = { config -> viewModel.onEvent(Top100UiEvent.OnSortConfigChanged(config)) }
    )
}
