package com.abn.amro.movies.ui.feature.detail.presentation

import com.abn.amro.core.common.helper.toTmdbUrl
import com.abn.amro.core.common.model.TmdbImageSize
import com.abn.amro.core.ui.model.UiText
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.domain.model.MovieDetail
import com.abn.amro.movies.ui.R
import com.abn.amro.movies.ui.feature.detail.mapper.toUiModel
import com.abn.amro.movies.ui.feature.detail.model.MovieDetailUiModel
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class MovieDetailUiMapperTest {

    @Test
    fun `maps fields and uses correct formatted UiText types`() {
        val domain = MovieDetail(
            id = 123,
            title = "Inception",
            tagline = "Dreams and all that",
            overview = "People dream within a dream",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2010-07-16",
            voteAverage = 8.8,
            voteCount = 20_000,
            budget = 160_000_000,
            revenue = 9_018_237_811,
            status = "Released",
            runtime = 148,
            imdbId = "tt1375666",
            genres = listOf(Genre(1, "Sci-Fi"), Genre(2, "Action"))
        )

        val expected = MovieDetailUiModel(
            id = 123,
            title = "Inception",
            tagline = "Dreams and all that",
            posterUrl = "/poster.jpg".toTmdbUrl(TmdbImageSize.POSTER_MEDIUM),
            backdropUrl = "/backdrop.jpg".toTmdbUrl(TmdbImageSize.BACKDROP_LARGE),
            genres = listOf("Sci-Fi", "Action"),
            overview = "People dream within a dream",

            voteAverage = UiText.LocalizedDecimal(8.8),
            voteCount = UiText.PluralResource(
                resId = R.plurals.votes_count,
                quantity = 20_000,
                formatArgs = listOf(UiText.LocalizedNumber(20_000L))
            ),
            budget = UiText.LocalizedCurrency(160_000_000),
            revenue = UiText.LocalizedCurrency(9_018_237_811),
            status = "Released",
            runtime = UiText.RuntimeMinutes(148),
            releaseDate = UiText.LocalizedDateIso("2010-07-16"),
            imdbUrl = "https://www.imdb.com/title/tt1375666"
        )

        val actual = domain.toUiModel()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `backdrop falls back to poster path when backdrop is missing`() {
        val domain = baseDomain(
            posterPath = "/poster.jpg",
            backdropPath = null
        )

        val actual = domain.toUiModel()

        assertThat(actual.posterUrl).isEqualTo("/poster.jpg".toTmdbUrl(TmdbImageSize.POSTER_MEDIUM))
        assertThat(actual.backdropUrl).isEqualTo("/poster.jpg".toTmdbUrl(TmdbImageSize.BACKDROP_LARGE))
    }

    @Test
    fun `nulling rules - blanks and non-positive values become null`() {
        val domain = baseDomain(
            tagline = "   ",
            status = "",
            imdbId = " ",
            posterPath = null,
            backdropPath = null,
            releaseDate = null,
            runtime = 0,
            budget = 0,
            revenue = -1,
            genres = emptyList()
        )

        val actual = domain.toUiModel()

        assertThat(actual.tagline).isNull()
        assertThat(actual.status).isNull()
        assertThat(actual.imdbUrl).isNull()

        assertThat(actual.posterUrl).isNull()
        assertThat(actual.backdropUrl).isNull()

        assertThat(actual.releaseDate).isNull()
        assertThat(actual.runtime).isNull()
        assertThat(actual.budget).isNull()
        assertThat(actual.revenue).isNull()

        assertThat(actual.genres).isEmpty()
    }

    private fun baseDomain(
        id: Long = 123,
        title: String = "Inception",
        tagline: String? = "Tagline",
        overview: String = "Overview",
        posterPath: String? = "/poster.jpg",
        backdropPath: String? = "/backdrop.jpg",
        genres: List<Genre> = listOf(Genre(1, "Sci-Fi")),
        voteAverage: Double = 8.8,
        voteCount: Int = 20_000,
        budget: Long = 160_000_000,
        revenue: Long = 500_000_000,
        status: String = "Released",
        imdbId: String? = "tt123",
        runtime: Int = 148,
        releaseDate: String? = "2010-07-16"
    ) = MovieDetail(
        id = id,
        title = title,
        tagline = tagline,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        genres = genres,
        voteAverage = voteAverage,
        voteCount = voteCount,
        budget = budget,
        revenue = revenue,
        status = status,
        imdbId = imdbId,
        runtime = runtime,
        releaseDate = releaseDate
    )
}