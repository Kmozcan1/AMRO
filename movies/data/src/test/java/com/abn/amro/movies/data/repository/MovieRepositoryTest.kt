package com.abn.amro.movies.data.repository

import app.cash.turbine.test
import com.abn.amro.core.common.result.AmroResult
import com.abn.amro.movies.data.remote.TmdbApiService
import com.abn.amro.movies.data.remote.model.GenreDto
import com.abn.amro.movies.data.remote.model.MovieDto
import com.abn.amro.movies.data.remote.response.GenresResponse
import com.abn.amro.movies.data.remote.response.MovieDetailResponse
import com.abn.amro.movies.data.remote.response.TrendingMoviesResult
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class MovieRepositoryTest {

    private val api: TmdbApiService = mockk()

    @Test
    fun `getTop100Movies emits Loading then Success, aggregates 5 pages in request order, and dedupes by id`() =
        runTest {
            val repository = createRepository(testScheduler)

            val movieA = movieDto(id = 1, title = "P1", posterPath = "/img1.jpg")
            val movieB = movieDto(id = 2, title = "P2-first", posterPath = "/img2_first.jpg")
            val movieBDuplicate = movieDto(id = 2, title = "P2-dup", posterPath = "/img2_dup.jpg")
            val movieC = movieDto(id = 3, title = "P3", posterPath = "/img3.jpg")
            val movieD = movieDto(id = 4, title = "P4", posterPath = "/img4.jpg")
            val movieE = movieDto(id = 5, title = "P5", posterPath = "/img5.jpg")

            coEvery { api.getTrendingMovies(page = 1) } returns
                    trendingResponse(page = 1, results = listOf(movieA))
            coEvery { api.getTrendingMovies(page = 2) } returns
                    trendingResponse(page = 2, results = listOf(movieB, movieBDuplicate))
            coEvery { api.getTrendingMovies(page = 3) } returns
                    trendingResponse(page = 3, results = listOf(movieC))
            coEvery { api.getTrendingMovies(page = 4) } returns
                    trendingResponse(page = 4, results = listOf(movieD))
            coEvery { api.getTrendingMovies(page = 5) } returns
                    trendingResponse(page = 5, results = listOf(movieE))

            repository.getTop100Movies().test(timeout = TIMEOUT) {
                assertThat(awaitItem()).isEqualTo(AmroResult.Loading)

                testScheduler.advanceUntilIdle()

                val result = awaitItem() as AmroResult.Success
                val data = result.data

                assertThat(data).hasSize(5)
                assertThat(data.count { it.id == 2L }).isEqualTo(1)

                // distinctBy keeps first occurrence
                val kept = data.first { it.id == 2L }
                assertThat(kept.title).isEqualTo("P2-first")
                assertThat(kept.posterPath).isEqualTo("/img2_first.jpg")

                assertThat(data.map { it.id }).containsExactly(1L, 2L, 3L, 4L, 5L).inOrder()

                awaitComplete()
            }

            coVerify(exactly = 1) { api.getTrendingMovies(page = 1) }
            coVerify(exactly = 1) { api.getTrendingMovies(page = 2) }
            coVerify(exactly = 1) { api.getTrendingMovies(page = 3) }
            coVerify(exactly = 1) { api.getTrendingMovies(page = 4) }
            coVerify(exactly = 1) { api.getTrendingMovies(page = 5) }
        }

    @Test
    fun `getTop100Movies emits Loading then Error when any page fails`() = runTest {
        val repository = createRepository(testScheduler)

        // Fail page 3
        coEvery { api.getTrendingMovies(page = any()) } answers {
            val page = firstArg<Int>()

            if (page == 3) {
                throw IOException("Timeout")
            } else {
                trendingResponse(page, emptyList())
            }
        }

        repository.getTop100Movies().test(timeout = TIMEOUT) {
            assertThat(awaitItem()).isEqualTo(AmroResult.Loading)

            testScheduler.advanceUntilIdle()

            val err = awaitItem() as AmroResult.Error
            assertThat(err.exception).isInstanceOf(IOException::class.java)
            assertThat(err.exception.message).isEqualTo("Timeout")

            awaitComplete()
        }

        coVerify(exactly = 1) { api.getTrendingMovies(page = 3) }
    }

    @Test
    fun `getMovieGenres emits Loading then Success and dedupes by id`() = runTest {
        val repository = createRepository(testScheduler)

        val g1 = GenreDto(1, "Action")
        val g1dup = GenreDto(1, "Action Copy")
        val g2 = GenreDto(2, "Comedy")

        coEvery { api.getMovieGenres() } returns GenresResponse(genres = listOf(g1, g1dup, g2))

        repository.getMovieGenres().test(timeout = TIMEOUT) {
            assertThat(awaitItem()).isEqualTo(AmroResult.Loading)

            testScheduler.advanceUntilIdle()

            val result = awaitItem() as AmroResult.Success
            val genres = result.data

            assertThat(genres.map { it.id }).containsExactly(1, 2).inOrder()
            assertThat(genres.first { it.id == 1 }.name).isEqualTo("Action") // first kept

            awaitComplete()
        }

        coVerify(exactly = 1) { api.getMovieGenres() }
    }

    @Test
    fun `getMovieGenres emits Loading then Error on failure`() = runTest {
        val repository = createRepository(testScheduler)

        coEvery { api.getMovieGenres() } throws RuntimeException("Boom")

        repository.getMovieGenres().test(timeout = TIMEOUT) {
            assertThat(awaitItem()).isEqualTo(AmroResult.Loading)

            testScheduler.advanceUntilIdle()

            val err = awaitItem() as AmroResult.Error
            assertThat(err.exception).isInstanceOf(RuntimeException::class.java)
            assertThat(err.exception.message).isEqualTo("Boom")

            awaitComplete()
        }

        coVerify(exactly = 1) { api.getMovieGenres() }
    }

    @Test
    fun `getMovieDetails emits Loading then Success and preserves raw paths`() = runTest {
        val repository = createRepository(testScheduler)

        val response = movieDetailResponse(
            id = 123,
            title = "Inception",
            posterPath = "/poster.jpg",
            runtime = 148
        )
        coEvery { api.getMovieDetails(movieId = 123) } returns response

        repository.getMovieDetails(123).test(timeout = TIMEOUT) {
            assertThat(awaitItem()).isEqualTo(AmroResult.Loading)

            testScheduler.advanceUntilIdle()

            val result = awaitItem() as AmroResult.Success
            val movie = result.data

            assertThat(movie.id).isEqualTo(123)
            assertThat(movie.title).isEqualTo("Inception")
            assertThat(movie.runtime).isEqualTo(148)
            assertThat(movie.posterPath).isEqualTo("/poster.jpg")

            awaitComplete()
        }

        coVerify(exactly = 1) { api.getMovieDetails(movieId = 123) }
    }

    @Test
    fun `getMovieDetails emits Loading then Error on failure`() = runTest {
        val repository = createRepository(testScheduler)

        coEvery { api.getMovieDetails(movieId = 999) } throws IOException("404")

        repository.getMovieDetails(999).test(timeout = TIMEOUT) {
            assertThat(awaitItem()).isEqualTo(AmroResult.Loading)

            testScheduler.advanceUntilIdle()

            val err = awaitItem() as AmroResult.Error
            assertThat(err.exception).isInstanceOf(IOException::class.java)
            assertThat(err.exception.message).isEqualTo("404")

            awaitComplete()
        }

        coVerify(exactly = 1) { api.getMovieDetails(movieId = 999) }
    }

    private fun createRepository(scheduler: TestCoroutineScheduler): MovieRepositoryImpl {
        val dispatcher = StandardTestDispatcher(scheduler)
        return MovieRepositoryImpl(api = api, ioDispatcher = dispatcher)
    }

    private fun trendingResponse(page: Int, results: List<MovieDto>) = TrendingMoviesResult(
        page = page,
        results = results,
        totalPages = 10
    )

    private fun movieDto(id: Long, title: String, posterPath: String) = MovieDto(
        id = id,
        title = title,
        posterPath = posterPath,
        overview = "",
        releaseDate = "",
        voteAverage = 0.0,
        voteCount = 0,
        popularity = 0.0,
        backdropPath = null,
        genreIds = emptyList()
    )

    private fun movieDetailResponse(id: Long, title: String, posterPath: String, runtime: Int) =
        MovieDetailResponse(
            id = id,
            title = title,
            runtime = runtime,
            tagline = null,
            overview = null,
            posterPath = posterPath,
            backdropPath = null,
            genres = emptyList(),
            voteAverage = 0.0,
            voteCount = 0,
            budget = 0,
            revenue = 0,
            status = null,
            imdbId = null,
            releaseDate = null
        )

    private companion object {
        val TIMEOUT = 5.seconds
    }
}
