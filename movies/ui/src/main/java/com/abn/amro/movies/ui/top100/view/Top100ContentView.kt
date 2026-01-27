package com.abn.amro.movies.ui.top100.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.ui.model.MovieUiModel
import com.abn.amro.movies.ui.top100.presentation.*

@Composable
fun Top100ContentView(
    state: Top100UiState.Success,
    onGenreClick: (Int?) -> Unit,
    onSortConfigChanged: (Top100SortConfig) -> Unit,
    onMovieClick: (Long) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.selectedGenreId, state.sortConfig) {
        if (state.movies.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                GenreFilterRow(
                    genres = state.availableGenres,
                    selectedGenreId = state.selectedGenreId,
                    onGenreClick = onGenreClick
                )
            }
            SortButton(
                currentConfig = state.sortConfig,
                onConfigChanged = onSortConfigChanged
            )
        }

        if (state.movies.isEmpty()) {
            EmptyView()
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = state.movies,
                    key = { it.id }
                ) { movie ->
                    MovieItem(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
        }
    }
}

@Composable
fun SortButton(
    currentConfig: Top100SortConfig,
    onConfigChanged: (Top100SortConfig) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Text(
                "Sort By",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            SortType.entries.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(type.labelRes),
                            fontWeight = if (currentConfig.type == type) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onConfigChanged(currentConfig.copy(type = type))
                        expanded = false
                    }
                )
            }

            HorizontalDivider()

            Text(
                "Order",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            SortOrder.entries.forEach { order ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(order.labelRes),
                            fontWeight = if (currentConfig.order == order) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onConfigChanged(currentConfig.copy(order = order))
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GenreFilterRow(
    genres: List<Genre>,
    selectedGenreId: Int?,
    onGenreClick: (Int?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedGenreId == null,
                onClick = { onGenreClick(null) },
                label = { Text("All") }
            )
        }
        items(genres, key = { it.id }) { genre ->
            FilterChip(
                selected = genre.id == selectedGenreId,
                onClick = { onGenreClick(genre.id) },
                label = { Text(genre.name) }
            )
        }
    }
}

@Composable
fun MovieItem(movie: MovieUiModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.height(140.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                movie.releaseDate?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "★ ${movie.voteAverage}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
