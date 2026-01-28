package com.abn.amro.movies.ui.feature.top100.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abn.amro.core.ui.component.AppChip
import com.abn.amro.core.ui.fadingEdge
import com.abn.amro.movies.domain.model.Genre
import com.abn.amro.movies.ui.feature.top100.presentation.SortOrder
import com.abn.amro.movies.ui.feature.top100.presentation.SortType
import com.abn.amro.movies.ui.feature.top100.presentation.Top100SortConfig

@Composable
fun Top100Header(
    genres: List<Genre>,
    selectedGenreId: Int?,
    sortConfig: Top100SortConfig,
    onGenreClick: (Int?) -> Unit,
    onSortConfigChanged: (Top100SortConfig) -> Unit,
    contentColor: Color,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GenreFilterRow(
            genres = genres,
            selectedGenreId = selectedGenreId,
            onGenreClick = onGenreClick,
            accentColor = accentColor,
            contentColor = contentColor,
            modifier = Modifier.weight(1f)
        )
        SortButton(
            currentConfig = sortConfig,
            onConfigChanged = onSortConfigChanged,
            contentColor = contentColor,
            backgroundColor = accentColor
        )
    }
}

@Composable
private fun GenreFilterRow(
    genres: List<Genre>,
    selectedGenreId: Int?,
    onGenreClick: (Int?) -> Unit,
    accentColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fadingEdge(
            horizontalGradient(0.85f to Color.Black, 1.0f to Color.Transparent)
        ),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            AppChip(
                label = "All",
                selected = selectedGenreId == null,
                onClick = { onGenreClick(null) },
                accentColor = accentColor,
                contentColor = contentColor
            )
        }
        items(genres, { it.id }) { genre ->
            AppChip(
                label = genre.name,
                selected = genre.id == selectedGenreId,
                onClick = { onGenreClick(genre.id) },
                accentColor = accentColor,
                contentColor = contentColor
            )
        }
    }
}

@Composable
private fun SortButton(
    currentConfig: Top100SortConfig,
    onConfigChanged: (Top100SortConfig) -> Unit,
    contentColor: Color,
    backgroundColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.Tune, "Sort", tint = contentColor)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = backgroundColor,
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "Sort By",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.labelLarge,
                color = contentColor.copy(alpha = 0.7f)
            )

            SortType.entries.forEach { type ->
                val isSelected = currentConfig.type == type
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(type.labelRes),
                                modifier = Modifier.weight(1f),
                                color = contentColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                val rotation by animateFloatAsState(
                                    targetValue = if (currentConfig.order == SortOrder.Ascending) {
                                        180f
                                    } else {
                                        0f
                                    },
                                    animationSpec = tween(400),
                                    label = "SortFlip"
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Sort,
                                    contentDescription = null,
                                    tint = contentColor,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .graphicsLayer { rotationX = rotation }
                                )
                            }
                        }
                    },
                    onClick = {
                        val newOrder = if (isSelected) {
                            if (currentConfig.order == SortOrder.Ascending) {
                                SortOrder.Descending
                            } else {
                                SortOrder.Ascending
                            }
                        } else {
                            currentConfig.order
                        }
                        onConfigChanged(currentConfig.copy(type = type, order = newOrder))
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewTop100Header_AllSelected() {
    Top100Header(
        genres = listOf(
            Genre(id = 1, name = "Action"),
            Genre(id = 2, name = "Adventure"),
            Genre(id = 3, name = "Comedy")
        ),
        selectedGenreId = null,
        sortConfig = Top100SortConfig(type = SortType.POPULARITY, order = SortOrder.Descending),
        onGenreClick = {},
        onSortConfigChanged = {},
        contentColor = Color.Black,
        accentColor = Color(0xFF009287)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PreviewTop100Header_GenreSelected() {
    Top100Header(
        genres = listOf(
            Genre(id = 1, name = "Action"),
            Genre(id = 2, name = "Adventure"), Genre(id = 3, name = "Comedy")),
        selectedGenreId = 2, // "Adventure" Selected
        sortConfig = Top100SortConfig(type = SortType.TITLE, order = SortOrder.Ascending),
        onGenreClick = {},
        onSortConfigChanged = {},
        contentColor = Color.White,
        accentColor = Color(0xFF009287)
    )
}