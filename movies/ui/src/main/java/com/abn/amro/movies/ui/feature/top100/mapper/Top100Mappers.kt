package com.abn.amro.movies.ui.feature.top100.mapper

import com.abn.amro.core.common.helper.toTmdbUrl
import com.abn.amro.core.common.model.TmdbImageSize
import com.abn.amro.core.ui.UiText
import com.abn.amro.movies.domain.model.Movie
import com.abn.amro.movies.ui.R
import com.abn.amro.movies.ui.model.MovieUiModel

fun Movie.toUiModel(): MovieUiModel {
    return MovieUiModel(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.toTmdbUrl(TmdbImageSize.POSTER_SMALL),
        releaseDate = releaseDate?.let { UiText.LocalizedDateIso(it) }
            ?: UiText.StringResource(R.string.unknown),
        voteAverage = UiText.LocalizedDecimal(voteAverage, fractionDigits = 1),
        genreIds = genreIds
    )
}