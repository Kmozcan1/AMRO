package com.abn.amro.movies.ui.feature.detail.mapper

import com.abn.amro.core.common.helper.toTmdbUrl
import com.abn.amro.core.common.model.TmdbImageSize
import com.abn.amro.core.ui.UiText
import com.abn.amro.movies.domain.model.MovieDetail
import com.abn.amro.movies.ui.R
import com.abn.amro.movies.ui.model.MovieDetailUiModel
import javax.inject.Inject

class MovieDetailUiMapper @Inject constructor() {
    fun map(domain: MovieDetail): MovieDetailUiModel {
        val poster = domain.posterPath?.toTmdbUrl(TmdbImageSize.POSTER_MEDIUM)

        val backdrop = (domain.backdropPath ?: domain.posterPath)
            ?.toTmdbUrl(TmdbImageSize.BACKDROP_LARGE)

        return MovieDetailUiModel(
            id = domain.id,
            title = domain.title,
            tagline = domain.tagline?.takeIf { it.isNotBlank() },
            posterUrl = poster,
            backdropUrl = backdrop,
            genres = domain.genres.map { it.name },
            overview = domain.overview,
            voteAverage = UiText.LocalizedDecimal(domain.voteAverage),
            voteCount = UiText.PluralResource(
                resId = R.plurals.votes_count,
                quantity = domain.voteCount,
                formatArgs = listOf(UiText.LocalizedNumber(domain.voteCount.toLong()))
            ),
            budget = if (domain.budget > 0) {
                UiText.LocalizedCurrency(domain.budget)
            } else {
                null
            },
            revenue = if (domain.revenue > 0) {
                UiText.LocalizedCurrency(domain.revenue)
            } else {
                null
            },
            status = domain.status.takeIf { it.isNotBlank() },
            runtime = if (domain.runtime > 0) {
                UiText.RuntimeMinutes(domain.runtime)
            } else {
                null
            },
            releaseDate = domain.releaseDate?.let { UiText.LocalizedDateIso(it) },
            imdbUrl = domain.imdbId?.takeIf { it.isNotBlank() }?.let { "$IMDB_BASE_URL$it" }
        )
    }
}

private const val IMDB_BASE_URL = "https://www.imdb.com/title/"