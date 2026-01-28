package com.abn.amro.movies.ui.feature.top100.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.abn.amro.core.ui.component.LoadingView
import com.abn.amro.core.ui.helper.UpdateStatusBarIcons
import com.abn.amro.core.ui.helper.contentColor
import com.abn.amro.core.ui.model.UiText
import com.abn.amro.core.ui.theme.AmroTeal
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.ui.R
import com.abn.amro.movies.ui.components.MovieEmptyView
import com.abn.amro.movies.ui.components.MovieErrorView
import com.abn.amro.movies.ui.feature.top100.presentation.Top100SortConfig
import com.abn.amro.movies.ui.feature.top100.presentation.Top100UiState
import com.abn.amro.movies.ui.model.MovieUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Top100Screen(
    state: Top100UiState,
    onRetry: () -> Unit,
    onGenreClick: (Int?) -> Unit,
    onMovieClick: (Long, Int) -> Unit,
    onSortConfigChanged: (Top100SortConfig) -> Unit
) {
    val listState = rememberLazyListState()
    val colorCache = remember { mutableStateMapOf<Long, Color>() }
    val successState = state as? Top100UiState.Success

    val previousFilters = remember {
        mutableStateOf(successState?.sortConfig to successState?.selectedGenreId)
    }

    LaunchedEffect(successState?.sortConfig, successState?.selectedGenreId) {
        val currentFilters = successState?.sortConfig to successState?.selectedGenreId

        if (successState != null && currentFilters != previousFilters.value) {
            listState.scrollToItem(0)
        }

        previousFilters.value = currentFilters
    }

    val defaultAtmosphere = AmroTeal
    var stickyColor by rememberSaveable(stateSaver = ColorSaver) {
        mutableStateOf(defaultAtmosphere)
    }

    val currentAtmosphereColor by remember(successState) {
        derivedStateOf {
            val visibleIndex = listState.firstVisibleItemIndex
            val movieId =
                successState?.movies?.getOrNull(visibleIndex)?.id ?: return@derivedStateOf null
            colorCache[movieId]
        }
    }

    LaunchedEffect(currentAtmosphereColor) {
        currentAtmosphereColor?.let { stickyColor = it }
    }

    val animatedAtmosphere by animateColorAsState(
        targetValue = stickyColor,
        animationSpec = tween(800),
        label = "Atmosphere"
    )
    val topBarColor by animateColorAsState(
        targetValue = animatedAtmosphere.contentColor(),
        animationSpec = tween(800),
        label = "Text"
    )

    UpdateStatusBarIcons(backgroundColor = animatedAtmosphere)

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .background(
                    Brush.verticalGradient(
                        0.0f to animatedAtmosphere,
                        0.25f to animatedAtmosphere,
                        1.0f to MaterialTheme.colorScheme.background
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.top100_title),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = topBarColor,
                        actionIconContentColor = topBarColor
                    )
                )
            }
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                if (successState != null) {
                    Column {
                        Top100Header(
                            genres = successState.availableGenres,
                            selectedGenreId = successState.selectedGenreId,
                            sortConfig = successState.sortConfig,
                            onGenreClick = onGenreClick,
                            onSortConfigChanged = onSortConfigChanged,
                            contentColor = topBarColor,
                            accentColor = animatedAtmosphere
                        )

                        if (successState.movies.isEmpty()) {
                            MovieEmptyView(tint = stickyColor)
                        } else {
                            Top100List(
                                movies = successState.movies,
                                listState = listState,
                                colorCache = colorCache,
                                atmosphereColor = animatedAtmosphere,
                                onMovieClick = { movieId ->
                                    val clickedColor = colorCache[movieId] ?: stickyColor
                                    stickyColor = clickedColor
                                    onMovieClick(movieId, clickedColor.toArgb())
                                }
                            )
                        }
                    }
                }

                when (state) {
                    is Top100UiState.Loading -> {
                        if (successState == null) {
                            LoadingView(
                                modifier = Modifier.align(Alignment.Center),
                                tint = stickyColor
                            )
                        }
                    }

                    is Top100UiState.Error -> {
                        MovieErrorView(tint = stickyColor, error = state.error, onRetry = onRetry)
                    }

                    else -> Unit
                }
            }
        }
    }
}

val ColorSaver = Saver<Color, Int>(
    save = { it.toArgb() },
    restore = { Color(it) }
)

@Preview(showBackground = true)
@Composable
private fun PreviewTop100Screen_EmptyResult() {
    val genres = listOf(Genre(1, "Action"), Genre(2, "Comedy"))

    Top100Screen(
        state = Top100UiState.Success(
            movies = emptyList(),
            availableGenres = genres,
            selectedGenreId = 2,
            sortConfig = Top100SortConfig()
        ),
        onRetry = {}, onGenreClick = {}, onMovieClick = { _, _ -> }, onSortConfigChanged = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewTop100Screen_Success() {
    val movies = (1..5).map {
        MovieUiModel(
            id = it.toLong(),
            title = "Movie $it",
            overview = "Overview",
            posterUrl = null,
            releaseDate = UiText.DynamicString("2024"),
            voteAverage = UiText.DynamicString("8.5"),
            voteCount = UiText.DynamicString("1.2k"),
            genres = listOf("Action", "Drama")
        )
    }
    val genres = listOf(Genre(1, "Action"), Genre(2, "Comedy"))

    Top100Screen(
        state = Top100UiState.Success(movies, genres, null, Top100SortConfig()),
        onRetry = {}, onGenreClick = {}, onMovieClick = { _, _ -> }, onSortConfigChanged = {}
    )
}