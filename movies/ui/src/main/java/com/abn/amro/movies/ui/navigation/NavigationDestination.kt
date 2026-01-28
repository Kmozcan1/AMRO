package com.abn.amro.movies.ui.navigation

import com.abn.amro.core.common.navigation.NavigationDestination

object Top100Destination : NavigationDestination {
    override val route = "top100_route"
    override val title = "Top100 Movies"
}

object MovieDetailDestination : NavigationDestination {
    const val ARG_MOVIE_ID = "movieId"
    const val ARG_COLOR = "color"
    private const val BASE_ROUTE = "movie_detail_route"

    override val route = "$BASE_ROUTE/{$ARG_MOVIE_ID}?$ARG_COLOR={$ARG_COLOR}"

    override val title = "Movie Details"

    fun createNavigationRoute(movieId: Long, color: Int): String {
        return "$BASE_ROUTE/$movieId?$ARG_COLOR=$color"
    }
}