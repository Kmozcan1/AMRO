package com.abn.amro.movies.ui.feature.detail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.abn.amro.movies.ui.navigation.MovieDetailDestination

fun NavGraphBuilder.movieDetailScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = MovieDetailDestination.route,
        arguments = listOf(
            navArgument(MovieDetailDestination.ARG_MOVIE_ID) { type = NavType.LongType },
            navArgument(MovieDetailDestination.ARG_COLOR) {
                type = NavType.IntType
                defaultValue = 0
            }
        )
    ) { backStackEntry ->
        val color = backStackEntry.arguments?.getInt(MovieDetailDestination.ARG_COLOR) ?: 0

        DetailRoute(
            initialColor = color,
            onNavigateBack = onNavigateBack
        )
    }
}
