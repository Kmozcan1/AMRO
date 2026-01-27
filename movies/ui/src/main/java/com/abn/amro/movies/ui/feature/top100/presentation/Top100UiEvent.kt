package com.abn.amro.movies.ui.feature.top100.presentation

sealed interface Top100UiEvent {
    data class OnGenreSelected(val genreId: Int?) : Top100UiEvent
    data class OnSortConfigChanged(val config: Top100SortConfig) : Top100UiEvent
    data class OnMovieClicked(val movieId: Long) : Top100UiEvent
    data object OnRetry : Top100UiEvent
}
