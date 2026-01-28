package com.abn.amro.movies.ui.feature.top100.presentation

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.abn.amro.core.common.result.AmroResult
import com.abn.amro.core.testing.MainDispatcherExtension
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.domain.model.Movie
import com.abn.amro.movies.domain.repository.MovieRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
class Top100ViewModelTest {

    private val repository: MovieRepository = mockk()
    private lateinit var viewModel: Top100ViewModel

    private fun initViewModel() {
        viewModel = Top100ViewModel(repository)
    }

    @Test
    fun `when initialized successfully, then applies default sort (Popularity Desc)`() = runTest {
        val movies = createMovies()
        val genres = createGenres()
        every { repository.getTop100Movies() } returns flowOf(AmroResult.Success(movies))
        every { repository.getMovieGenres() } returns flowOf(AmroResult.Success(genres))

        initViewModel()

        viewModel.uiState.test {
            val state = awaitSuccess()

            assertThat(state.sortConfig.type).isEqualTo(SortType.POPULARITY)
            assertThat(state.sortConfig.order).isEqualTo(SortOrder.Descending)
            assertThat(state.selectedGenreId).isNull()

            assertThat(state.movies.map { it.title })
                .containsExactly("Comedy Movie", "Action Movie 1", "Action Movie 2")
                .inOrder()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when genre selected, then filters movies`() = runTest {
        val movies = createMovies()
        val genres = createGenres()
        every { repository.getTop100Movies() } returns flowOf(AmroResult.Success(movies))
        every { repository.getMovieGenres() } returns flowOf(AmroResult.Success(genres))

        initViewModel()

        viewModel.uiState.test {
            awaitSuccess()

            viewModel.onEvent(Top100UiEvent.OnGenreSelected(1)) // Select Action

            val filteredState = awaitSuccess { it.selectedGenreId == 1 }

            assertThat(filteredState.movies).hasSize(2)
            assertThat(filteredState.movies.map { it.title })
                .containsExactly("Action Movie 1", "Action Movie 2")
                .inOrder()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when sort changed, then reorders movies`() = runTest {
        val movies = createMovies()
        val genres = createGenres()
        every { repository.getTop100Movies() } returns flowOf(AmroResult.Success(movies))
        every { repository.getMovieGenres() } returns flowOf(AmroResult.Success(genres))

        initViewModel()

        viewModel.uiState.test {
            awaitSuccess()

            val newConfig = Top100SortConfig(SortType.TITLE, SortOrder.Descending)
            viewModel.onEvent(Top100UiEvent.OnSortConfigChanged(newConfig))

            val sortedState = awaitSuccess { it.sortConfig == newConfig }

            assertThat(sortedState.movies.map { it.title })
                .containsExactly("Comedy Movie", "Action Movie 2", "Action Movie 1")
                .inOrder()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when movie clicked, then navigates to detail`() = runTest {
        every { repository.getTop100Movies() } returns flowOf(AmroResult.Success(emptyList()))
        every { repository.getMovieGenres() } returns flowOf(AmroResult.Success(emptyList()))
        initViewModel()

        viewModel.effects.test {
            viewModel.onEvent(Top100UiEvent.OnMovieClicked(movieId = 100, color = 123456))

            val effect = awaitItem() as Top100UiEffect.NavigateToDetail
            assertThat(effect.movieId).isEqualTo(100)
            assertThat(effect.color).isEqualTo(123456)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when repository returns error, then emits Error state`() = runTest {
        every { repository.getTop100Movies() } returns flowOf(AmroResult.Error(Exception("Network Error")))
        every { repository.getMovieGenres() } returns flowOf(AmroResult.Success(emptyList()))

        initViewModel()

        viewModel.uiState.test {
            val first = awaitItem()
            val errorState = first as? Top100UiState.Error ?: awaitItem() as Top100UiState.Error

            assertThat(errorState.error).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<Top100UiState>.awaitSuccess(
        predicate: (Top100UiState.Success) -> Boolean = { true }
    ): Top100UiState.Success {
        var item = awaitItem()
        if (item is Top100UiState.Loading) {
            item = awaitItem()
        }

        if (item is Top100UiState.Success && predicate(item)) {
            return item
        }

        throw AssertionError("Expected Success matching predicate, but got $item")
    }

    private fun createGenres() = listOf(
        Genre(1, "Action"),
        Genre(2, "Comedy")
    )

    private fun createMovies() = listOf(
        Movie(
            id = 1,
            title = "Action Movie 1",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2023-01-01",
            voteAverage = 8.0,
            voteCount = 100,
            popularity = 10.0,
            genreIds = listOf(1)
        ),
        Movie(
            id = 2,
            title = "Comedy Movie",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2023-01-02",
            voteAverage = 7.0,
            voteCount = 50,
            popularity = 20.0, // Highest Pop
            genreIds = listOf(2)
        ),
        Movie(
            id = 3,
            title = "Action Movie 2",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2023-01-03",
            voteAverage = 9.0,
            voteCount = 200,
            popularity = 5.0, // Lowest Pop
            genreIds = listOf(1)
        )
    )
}