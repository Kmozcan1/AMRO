package com.abn.amro.movies.ui.feature.top100.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.abn.amro.core.ui.component.AppBadge
import com.abn.amro.core.ui.helper.contentColor
import com.abn.amro.core.ui.model.UiText
import com.abn.amro.core.ui.model.asString
import com.abn.amro.movies.ui.model.MovieUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun Top100List(
    movies: List<MovieUiModel>,
    listState: LazyListState,
    colorCache: Map<Long, Color>,
    atmosphereColor: Color,
    onMovieClick: (Long) -> Unit
) {
    val density = LocalDensity.current

    val fadeThresholdPx = remember(density) { with(density) { 40.dp.toPx() } }
    val itemHeightPx = remember(density) { with(density) { 140.dp.toPx() } }
    val maxOverlayHeight = 80.dp

    val fadeAlpha by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            (offset / fadeThresholdPx).coerceIn(0f, 1f)
        }
    }

    val dynamicOverlayHeight by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            val remainingHeight = itemHeightPx - offset
            val heightInPx = min(remainingHeight, with(density) { maxOverlayHeight.toPx() })
            with(density) { heightInPx.coerceAtLeast(0f).toDp() }
        }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(movies, { it.id }) { movie ->
                MovieCard(
                    movie = movie,
                    cachedColor = colorCache[movie.id],
                    onColorExtracted = { (colorCache as MutableMap)[movie.id] = it },
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dynamicOverlayHeight)
                .alpha(fadeAlpha)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            atmosphereColor,
                            atmosphereColor.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
private fun MovieCard(
    movie: MovieUiModel,
    cachedColor: Color?,
    onColorExtracted: (Color) -> Unit,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val targetColor = cachedColor ?: Color(color = 0xFF1F1F1F)
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(500),
        label = "CardColor"
    )
    val contentColor = animatedColor.contentColor()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = animatedColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.fillMaxSize()) {
            PosterSection(movie.posterUrl, animatedColor) { bitmap ->
                scope.launch(Dispatchers.Default) {
                    val palette = Palette.from(bitmap).generate()
                    val swatch =
                        palette.dominantSwatch ?: palette.vibrantSwatch ?: palette.mutedSwatch

                    swatch?.let {
                        onColorExtracted(Color(it.rgb))
                    }
                }
            }
            MovieCardInfo(movie, contentColor)
        }
    }
}

@Composable
private fun PosterSection(
    url: String?,
    blendColor: Color,
    onBitmapLoaded: (android.graphics.Bitmap) -> Unit
) {
    Box(
        Modifier
            .width(100.dp)
            .fillMaxHeight()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true)
                .allowHardware(false).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = { onBitmapLoaded(it.result.drawable.toBitmap()) },
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0f to Color.Transparent,
                        0.80f to Color.Transparent,
                        1f to blendColor
                    )
                )
        )
    }
}

@Composable
private fun RowScope.MovieCardInfo(
    movie: MovieUiModel,
    contentColor: Color
) {
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxHeight()
            .weight(1f),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = contentColor
            )

            movie.releaseDate?.asString()?.let { date ->
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.8f),
                )
            }

            if (movie.genres.isNotEmpty()) {
                Text(
                    text = movie.genres.joinToString(" • "),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }


        AppBadge(
            "${movie.voteAverage.asString()} (${movie.voteCount.asString() ?: "0"})",
            contentColor,
            icon = Icons.Filled.Star,
            useBorder = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMovieCard_Normal() {
    val movie = MovieUiModel(
        id = 1,
        title = "Inception",
        overview = "Dreams.",
        posterUrl = null,
        releaseDate = UiText.DynamicString("2010"),
        voteAverage = UiText.DynamicString("8.8"),
        voteCount = UiText.DynamicString("34k"),
        genres = listOf("Action", "Sci-Fi")
    )
    MovieCard(movie = movie, cachedColor = Color.DarkGray, onColorExtracted = {}, onClick = {})
}

@Preview(showBackground = true)
@Composable
private fun PreviewMovieCard_LongText() {
    val movie = MovieUiModel(
        id = 2,
        title = "Borat: Cultural Learnings of America for Make Benefit Glorious Nation of Kazakhstan",
        overview = "Very nice!",
        posterUrl = null,
        releaseDate = UiText.DynamicString("2006"),
        voteAverage = UiText.DynamicString("7.4"),
        voteCount = UiText.DynamicString("200k"),
        genres = listOf("Comedy", "Documentary", "International", "Mockumentary")
    )
    MovieCard(movie = movie, cachedColor = Color.Blue, onColorExtracted = {}, onClick = {})
}