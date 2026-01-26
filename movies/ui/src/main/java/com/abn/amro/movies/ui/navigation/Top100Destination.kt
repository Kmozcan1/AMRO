package com.abn.amro.movies.ui.navigation

import com.abn.amro.common.navigation.NavigationDestination

object Top100Destination : NavigationDestination {
    override val route = "top100_route"
    override val title = "Trending Movies"
}

object MovieDetailDestination : NavigationDestination {
    const val ARG_MOVIE_ID = "movieId"
    private const val BASE_ROUTE = "movie_detail_route"
    
    override val route = "$BASE_ROUTE/{$ARG_MOVIE_ID}"
    override val title = "Movie Details"

    fun createNavigationRoute(movieId: Int) = "$BASE_ROUTE/$movieId"
}