import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.abn.amro.movies.ui.feature.detail.navigation.DetailRoute
import com.abn.amro.movies.ui.navigation.MovieDetailDestination

fun NavGraphBuilder.movieDetailScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = MovieDetailDestination.route,
    ) {
        DetailRoute(onNavigateBack = onNavigateBack)
    }
}