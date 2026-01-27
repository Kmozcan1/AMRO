package com.abn.amro.movies.ui.feature.detail.presentation

import com.abn.amro.core.common.model.AmroError
import com.abn.amro.movies.ui.model.MovieDetailUiModel

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Error(val error: AmroError) : DetailUiState
    data class Success(val movie: MovieDetailUiModel) : DetailUiState
}

sealed interface DetailUiEvent {
    data object OnRetry : DetailUiEvent
    data object OnBackClicked : DetailUiEvent
    data object OnImdbClicked : DetailUiEvent
}

sealed interface DetailUiEffect {
    data object NavigateBack : DetailUiEffect
    data class OpenUrl(val url: String) : DetailUiEffect
}