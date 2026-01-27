package com.abn.amro.movies.ui.top100.presentation

sealed interface Top100UiEffect {
    data class NavigateToDetail(val movieId: Long) : Top100UiEffect
    data class ShowToast(val message: String) : Top100UiEffect
}