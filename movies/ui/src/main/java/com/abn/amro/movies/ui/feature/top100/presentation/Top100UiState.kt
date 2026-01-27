package com.abn.amro.movies.ui.feature.top100.presentation

import com.abn.amro.core.common.model.AmroError
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.ui.model.MovieUiModel

sealed interface Top100UiState {
    data object Loading : Top100UiState

    data class Error(val error: Top100UiError) : Top100UiState

    data class Success(
        val movies: List<MovieUiModel>,
        val availableGenres: List<Genre> = emptyList(),
        val selectedGenreId: Int? = null,
        val sortConfig: Top100SortConfig = Top100SortConfig()
    ) : Top100UiState
}

sealed interface Top100UiError {
    data class Common(val error: AmroError) : Top100UiError
    data object Empty : Top100UiError
}
