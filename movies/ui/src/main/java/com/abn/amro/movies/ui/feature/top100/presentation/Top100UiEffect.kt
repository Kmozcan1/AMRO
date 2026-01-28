package com.abn.amro.movies.ui.feature.top100.presentation

sealed interface Top100UiEffect {
    data class NavigateToDetail(val movieId: Long, val color: Int) : Top100UiEffect
}