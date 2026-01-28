package com.abn.amro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.abn.amro.core.ui.theme.AMROTheme
import com.abn.amro.movies.ui.feature.detail.navigation.movieDetailScreen
import com.abn.amro.movies.ui.feature.top100.navigation.top100Screen
import com.abn.amro.movies.ui.feature.top100.presentation.Top100UiState
import com.abn.amro.movies.ui.feature.top100.presentation.Top100ViewModel
import com.abn.amro.movies.ui.navigation.MovieDetailDestination
import com.abn.amro.movies.ui.navigation.Top100Destination
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel: Top100ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.uiState.value is Top100UiState.Loading
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AMROTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Top100Destination.route
                    ) {
                        top100Screen(
                            onNavigateToDetail = { movieId, color ->
                                navController.navigate(
                                    MovieDetailDestination.createNavigationRoute(movieId, color)
                                )
                            }
                        )

                        movieDetailScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}