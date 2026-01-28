package com.abn.amro.movies.ui.feature.top100.presentation

import com.abn.amro.core.common.model.AmroError
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.ui.model.MovieUiModel

sealed interface Top100UiState {
    data object Loading : Top100UiState

    data class Success(
        val movies: List<MovieUiModel>,
        val availableGenres: List<Genre>,
        val selectedGenreId: Int?,
        val sortConfig: Top100SortConfig
    ) : Top100UiState

    data class Error(val error: AmroError) : Top100UiState
}