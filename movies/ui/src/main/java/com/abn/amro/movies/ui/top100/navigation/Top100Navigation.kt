package com.abn.amro.movies.ui.top100.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.abn.amro.movies.ui.navigation.Top100Destination

fun NavGraphBuilder.top100Screen(
    onNavigateToDetail: (Long) -> Unit
) {
    composable(route = Top100Destination.route) {
        Top100Route(onNavigateToDetail = onNavigateToDetail)
    }
}