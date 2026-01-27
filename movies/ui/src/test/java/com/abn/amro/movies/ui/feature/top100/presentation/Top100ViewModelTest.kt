package com.abn.amro.movies.ui.feature.top100.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.abn.amro.core.common.result.AmroResult
import com.abn.amro.core.testing.MainDispatcherExtension
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.domain.model.MovieDetail
import com.abn.amro.movies.domain.repository.MovieRepository
import com.abn.amro.movies.ui.feature.detail.presentation.DetailUiEffect
import com.abn.amro.movies.ui.feature.detail.presentation.DetailUiEvent
import com.abn.amro.movies.ui.feature.detail.presentation.DetailUiState
import com.abn.amro.movies.ui.feature.detail.presentation.DetailViewModel
import com.abn.amro.movies.ui.feature.detail.mapper.MovieDetailUiMapper
import com.abn.amro.movies.ui.navigation.MovieDetailDestination
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
class DetailViewModelTest {

    private val repository: MovieRepository = mockk()

    private val mapper = MovieDetailUiMapper()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DetailViewModel

    private val movieId = 123L

    @BeforeEach
    fun setUp() {
        savedStateHandle = SavedStateHandle(
            mapOf(MovieDetailDestination.ARG_MOVIE_ID to movieId.toString())
        )
    }

    private fun initViewModel() {
        viewModel = DetailViewModel(
            savedStateHandle = savedStateHandle,
            repository = repository,
            mapper = mapper
        )
    }

    @Test
    fun `when initialized, then movie details are loaded successfully`() = runTest {
        val movie = createDetailedMovie(id = movieId)
        every { repository.getMovieDetails(movieId) } returns flowOf(AmroResult.Success(movie))

        initViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(DetailUiState.Loading)

            val state = awaitItem() as DetailUiState.Success
            assertThat(state.movie.title).isEqualTo("Inception")
            assertThat(state.movie.genres).contains("Sci-Fi")
        }
    }

    @Test
    fun `when IMDB clicked, then OpenUrl effect is emitted`() = runTest {
        val movie = createDetailedMovie(id = movieId, imdbId = "tt123")
        every { repository.getMovieDetails(movieId) } returns flowOf(AmroResult.Success(movie))

        initViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(DetailUiState.Loading)
            assertThat(awaitItem()).isInstanceOf(DetailUiState.Success::class.java)
        }

        viewModel.effects.test {
            viewModel.onEvent(DetailUiEvent.OnImdbClicked)

            val effect = awaitItem() as DetailUiEffect.OpenUrl
            assertThat(effect.url).isEqualTo("https://www.imdb.com/title/tt123")
        }
    }

    @Test
    fun `when repository returns error, then state is Error`() = runTest {
        every { repository.getMovieDetails(movieId) } returns flowOf(AmroResult.Error(Exception("boom")))

        initViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(DetailUiState.Loading)
            val state = awaitItem() as DetailUiState.Error
            assertThat(state.error).isNotNull()
        }
    }

    private fun createDetailedMovie(id: Long, imdbId: String? = "tt0133093") = MovieDetail(
        id = id,
        title = "Inception",
        overview = "Dream within a dream",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2010-07-16",
        voteAverage = 8.8,
        voteCount = 20000,
        tagline = "Mind blowing stuff",
        budget = 160000000,
        revenue = 828322032,
        status = "Released",
        runtime = 148,
        imdbId = imdbId,
        genres = listOf(Genre(1, "Sci-Fi"))
    )
}
