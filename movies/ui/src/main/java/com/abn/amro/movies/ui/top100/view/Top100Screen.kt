package com.abn.amro.movies.ui.top100.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abn.amro.core.common.model.AmroError
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.ui.R
import com.abn.amro.movies.ui.model.MovieUiModel
import com.abn.amro.movies.ui.top100.presentation.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Top100Screen(
    state: Top100UiState,
    onRetry: () -> Unit,
    onGenreClick: (Int?) -> Unit,
    onMovieClick: (Long) -> Unit,
    onSortConfigChanged: (Top100SortConfig) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.top100_title)) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                is Top100UiState.Loading -> LoadingView()
                is Top100UiState.Error -> ErrorView(state.error, onRetry)
                is Top100UiState.Success -> Top100ContentView(
                    state = state,
                    onGenreClick = onGenreClick,
                    onMovieClick = onMovieClick,
                    onSortConfigChanged = onSortConfigChanged
                )
            }
        }
    }
}

@Preview(name = "Content Success", showBackground = true)
@Composable
private fun Top100ScreenPreview() {
    val dummyMovies = listOf(
        MovieUiModel(
            id = 1,
            title = "The Batman",
            overview = "Edgy millionaire cosplays as a flying mammal.",
            posterUrl = "https://image.tmdb.org/t/p/w500/path.jpg",
            releaseDate = "2022-03-01",
            voteAverage = "8.5",
            genreIds = listOf(1),
        )
    )
    val dummyGenres = listOf(Genre(id = 1, name = "Action"))

    Top100Screen(
        state = Top100UiState.Success(
            movies = dummyMovies,
            availableGenres = dummyGenres,
            selectedGenreId = 1
        ),
        onRetry = {},
        onGenreClick = {},
        onMovieClick = {},
        onSortConfigChanged = {}
    )
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun Top100ErrorPreview() {
    Top100Screen(
        state = Top100UiState.Error(
            error = Top100UiError.Common(error = AmroError.Network)
        ),
        onRetry = {},
        onGenreClick = {},
        onMovieClick = {},
        onSortConfigChanged = {}
    )
}
