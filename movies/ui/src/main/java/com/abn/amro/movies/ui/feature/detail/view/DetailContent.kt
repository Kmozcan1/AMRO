package com.abn.amro.movies.ui.feature.detail.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.abn.amro.core.ui.UiText
import com.abn.amro.core.ui.asString
import com.abn.amro.core.ui.component.AppBadge
import com.abn.amro.core.ui.extension.contentColor
import com.abn.amro.movies.ui.model.MovieDetailUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DetailContent(
    movie: MovieDetailUiModel,
    initialColor: Int? = null,
    onImdbClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val startColor = if (initialColor != null && initialColor != 0) {
        Color(initialColor)
    } else {
        Color(0xFF1F1F1F)
    }
    var targetColor by remember { mutableStateOf(startColor) }

    val animatedBackgroundColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(1000),
        label = "BgMorph"
    )

    val contentColor = animatedBackgroundColor.contentColor()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(animatedBackgroundColor)
    ) {
        DetailHeader(movie = movie, blendColor = animatedBackgroundColor) { bitmap ->
            scope.launch(Dispatchers.Default) {
                val palette = Palette.from(bitmap).generate()
                val newColorInt = palette.dominantSwatch?.rgb ?: palette.vibrantSwatch?.rgb
                newColorInt?.let { targetColor = Color(it) }
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-48).dp)
        ) {
            HeroSection(movie, contentColor)
            StatsRow(movie, contentColor)
            BodySection(movie, contentColor)

            if (movie.imdbUrl != null) {
                ImdbButton(contentColor, animatedBackgroundColor, onImdbClick)
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun DetailHeader(
    movie: MovieDetailUiModel,
    blendColor: Color,
    onLoaded: (android.graphics.Bitmap) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(500.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.backdropUrl ?: movie.posterUrl)
                .crossfade(true)
                .allowHardware(false)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = { onLoaded(it.result.drawable.toBitmap()) },
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            blendColor.copy(alpha = 0.5f),
                            blendColor
                        ),
                        startY = 300f
                    )
                )
        )
    }
}

@Composable
private fun HeroSection(movie: MovieDetailUiModel, textColor: Color) {
    Text(
        text = movie.title,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Black,
        color = textColor
    )
    movie.tagline?.takeIf { it.isNotBlank() }?.let {
        Text(
            text = "“$it”",
            modifier = Modifier.padding(top = 8.dp),
            color = textColor.copy(alpha = 0.8f),
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
private fun StatsRow(movie: MovieDetailUiModel, textColor: Color) {
    Row(
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        movie.voteAverage?.let {
            val count = movie.voteCount?.asString() ?: "0"
            AppBadge(
                text = "${it.asString()} ($count)",
                contentColor = textColor,
                icon = Icons.Filled.Star,
                useBorder = true
            )
        }
        movie.runtime?.asString()?.let {
            AppBadge(
                text = it,
                contentColor = textColor,
                useBorder = true
            )
        }
        movie.status?.takeIf { it.isNotBlank() }?.let {
            AppBadge(
                text = it,
                contentColor = textColor,
                useBorder = true
            )
        }
    }
}

@Composable
private fun BodySection(movie: MovieDetailUiModel, textColor: Color) {
    if (!movie.genres.isNullOrEmpty()) {
        LabelText("Genres", textColor)
        Text(
            text = movie.genres.joinToString(" • "),
            color = textColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    movie.overview?.takeIf { it.isNotBlank() }?.let {
        LabelText("Overview", textColor)
        Text(
            text = it,
            modifier = Modifier.padding(top = 8.dp),
            color = textColor,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3f
        )
    }

    HorizontalDivider(Modifier.padding(vertical = 24.dp), color = textColor.copy(alpha = 0.2f))

    movie.budget?.asString()?.let {
        MetaRow(Icons.Default.AttachMoney, "Budget", it, textColor)
    }

    movie.revenue?.asString()?.let {
        MetaRow(Icons.Default.AttachMoney, "Revenue", it, textColor)
    }

    movie.releaseDate?.asString()?.let {
        MetaRow(Icons.Default.CalendarToday, "Release Date", it, textColor)
    }
}

@Composable
private fun LabelText(text: String, textColor: Color) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 24.dp),
        color = textColor.copy(alpha = 0.7f),
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun MetaRow(
    icon: ImageVector,
    label: String,
    value: String,
    textColor: Color
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(textColor.copy(alpha = 0.1f), MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = textColor
            )
        }
        Column(Modifier.padding(start = 16.dp)) {
            Text(
                label,
                color = textColor.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall
            )
            Text(text = value, color = textColor, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun ImdbButton(textColor: Color, backgroundColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(top = 32.dp)
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = textColor,
            contentColor = backgroundColor
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)

        Text(
            text = "View on IMDb",
            modifier = Modifier.padding(start = 8.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(name = "Normal Content", showBackground = true, backgroundColor = 0xFF1F1F1F)
@Composable
private fun PreviewDetailNormal() {
    val movie = MovieDetailUiModel(
        id = 1,
        title = "Inception",
        tagline = "Your mind is the scene of the crime.",
        overview = "Cobb, a skilled thief who commits corporate espionage by infiltrating the " +
                "subconscious of his targets is offered a chance to regain his old life.",
        posterUrl = null,
        backdropUrl = null,
        releaseDate = UiText.DynamicString("2010-07-15"),
        voteAverage = UiText.DynamicString("8.8"),
        voteCount = UiText.DynamicString("34k"),
        genres = listOf("Action", "Sci-Fi", "Thriller"),
        runtime = UiText.DynamicString("2h 28m"),
        status = "Released",
        budget = UiText.DynamicString("$160M"),
        revenue = UiText.DynamicString("$825M"),
        imdbUrl = "http://imdb.com"
    )
    DetailContent(movie = movie, initialColor = android.graphics.Color.DKGRAY, onImdbClick = {})
}

@Preview(name = "Edge Case: Long Text", showBackground = true)
@Composable
private fun PreviewDetailEdgeCase() {
    val movie = MovieDetailUiModel(
        id = 2,
        title = "Borat: Cultural Learnings of America " +
                "for Make Benefit Glorious Nation of Kazakhstan",
        tagline = "hello! hello! hello! hello! hello! hello! hello! hello!".repeat(
            3
        ),
        overview = "incredibly long. in fact this text is so long that it's ".repeat(
            3
        ),
        posterUrl = null,
        backdropUrl = null,
        releaseDate = UiText.DynamicString("2006-11-02"),
        voteAverage = UiText.DynamicString("7.3"),
        voteCount = UiText.DynamicString("150k"),
        genres = listOf(
            "Comedy",
            "Documentary",
            "Mockumentary",
            "Adventure",
            "International",
            "Cult Classic"
        ),
        runtime = UiText.DynamicString("1h 24m"),
        status = "Released",
        budget = null,
        revenue = null,
        imdbUrl = "http://imdb.com"
    )
    DetailContent(movie = movie, initialColor = android.graphics.Color.BLUE, onImdbClick = {})
}