package com.abn.amro.movies.feature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.abn.amro.movies.ui.feature.detail.navigation.movieDetailScreen
import com.abn.amro.movies.ui.feature.top100.navigation.top100Screen
import com.abn.amro.movies.ui.navigation.MovieDetailDestination
import com.abn.amro.movies.ui.navigation.Top100Destination

object MoviesFeature {
    val startDestination = Top100Destination.route

    fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController
    ) {
        navGraphBuilder.top100Screen(
            onNavigateToDetail = { movieId, color ->
                navController.navigate(
                    MovieDetailDestination.createNavigationRoute(movieId, color)
                )
            }
        )

        navGraphBuilder.movieDetailScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}
