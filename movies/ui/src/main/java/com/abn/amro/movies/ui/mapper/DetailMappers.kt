package com.abn.amro.movies.ui.mapper

import com.abn.amro.core.ui.UiText
import com.abn.amro.movies.domain.model.MovieDetail
import com.abn.amro.movies.ui.R
import com.abn.amro.movies.ui.formatRating
import com.abn.amro.movies.ui.model.MovieDetailUiModel
import java.text.NumberFormat
import java.util.Locale

fun MovieDetail.toDetailUiModel(imageBaseUrl: String): MovieDetailUiModel {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 0
    }

    val numberFormat = NumberFormat.getInstance()

    return MovieDetailUiModel(
        id = id,
        title = title,
        tagline = if (tagline.isNullOrBlank()) null else tagline,
        backdropUrl = (backdropPath ?: posterPath)?.let { "$imageBaseUrl$it" },
        posterUrl = posterPath?.let { "$imageBaseUrl$it" },
        genres = genres.joinToString(", ") { it.name },
        overview = overview,
        voteAverage = voteAverage.formatRating(),

        voteCount = UiText.PluralResource(
            resId = R.plurals.votes_count,
            quantity = voteCount,
            numberFormat.format(voteCount)
        ),

        budget = if (budget > 0) UiText.DynamicString(currencyFormat.format(budget))
        else UiText.StringResource(R.string.unknown),

        revenue = if (revenue > 0) UiText.DynamicString(currencyFormat.format(revenue))
        else UiText.StringResource(R.string.unknown),

        status = status,

        runtime = formatRuntime(runtime),

        releaseDate = releaseDate?.let { UiText.LocalizedDate(it) }
            ?: UiText.StringResource(R.string.unknown),

        imdbUrl = imdbId?.let { "https://www.imdb.com/title/$it" }
    )
}

private fun formatRuntime(minutes: Int): UiText {
    if (minutes <= 0) return UiText.StringResource(R.string.unknown)

    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    return if (hours > 0) {
        UiText.StringResource(R.string.runtime_format, hours, remainingMinutes)
    } else {
        UiText.StringResource(R.string.runtime_format_minutes, remainingMinutes)
    }
}