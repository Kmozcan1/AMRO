package com.abn.amro.movies.ui.feature.detail.mapper

import com.abn.amro.core.common.helper.toTmdbUrl
import com.abn.amro.core.common.model.TmdbImageSize
import com.abn.amro.core.ui.model.UiText
import com.abn.amro.movies.domain.model.MovieDetail
import com.abn.amro.movies.ui.R
import com.abn.amro.movies.ui.model.MovieDetailUiModel

fun MovieDetail.toUiModel(): MovieDetailUiModel {
    val poster = posterPath?.toTmdbUrl(TmdbImageSize.POSTER_MEDIUM)
    val backdrop = (backdropPath ?: posterPath)?.toTmdbUrl(TmdbImageSize.BACKDROP_LARGE)

    return MovieDetailUiModel(
        id = id,
        title = title,
        tagline = tagline?.takeIf { it.isNotBlank() },
        posterUrl = poster,
        backdropUrl = backdrop,
        genres = genres.map { it.name },
        overview = overview,
        voteAverage = UiText.LocalizedDecimal(voteAverage),
        voteCount = UiText.PluralResource(
            resId = R.plurals.votes_count,
            quantity = voteCount,
            formatArgs = listOf(UiText.LocalizedNumber(voteCount.toLong()))
        ),
        budget = if (budget > 0) UiText.LocalizedCurrency(budget) else null,
        revenue = if (revenue > 0) UiText.LocalizedCurrency(revenue) else null,

        status = status.takeIf { it.isNotBlank() },

        runtime = if (runtime > 0) UiText.RuntimeMinutes(runtime) else null,

        releaseDate = releaseDate?.let { UiText.LocalizedDateIso(it) },

        imdbUrl = imdbId?.takeIf { it.isNotBlank() }?.let { "https://www.imdb.com/title/$it" }
    )
}