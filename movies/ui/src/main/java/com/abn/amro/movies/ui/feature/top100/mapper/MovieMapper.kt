package com.abn.amro.movies.ui.feature.top100.mapper

import com.abn.amro.core.common.helper.toTmdbUrl
import com.abn.amro.core.common.model.TmdbImageSize
import com.abn.amro.core.ui.model.UiText
import com.abn.amro.movies.domain.model.Movie
import com.abn.amro.movies.ui.R
import com.abn.amro.movies.ui.feature.top100.model.MovieUiModel

fun Movie.toUiModel(genreMap: Map<Int, String>): MovieUiModel =
    MovieUiModel(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.toTmdbUrl(TmdbImageSize.POSTER_SMALL),
        releaseDate = releaseDate?.let { UiText.LocalizedDateIso(it) }
            ?: UiText.StringResource(R.string.unknown),
        voteAverage = UiText.LocalizedDecimal(voteAverage, fractionDigits = 1),
        voteCount = UiText.PluralResource(
            resId = R.plurals.votes_count,
            quantity = voteCount,
            formatArgs = listOf(UiText.LocalizedNumber(voteCount.toLong()))
        ),
        genres = genreIds.mapNotNull { id -> genreMap[id] }
    )
