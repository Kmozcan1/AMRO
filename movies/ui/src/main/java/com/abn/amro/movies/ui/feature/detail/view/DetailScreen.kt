package com.abn.amro.movies.ui.feature.detail.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.abn.amro.movies.ui.feature.detail.presentation.DetailUiState
import com.abn.amro.movies.ui.feature.top100.presentation.Top100UiError
import com.abn.amro.movies.ui.feature.top100.view.ErrorView
import com.abn.amro.movies.ui.feature.top100.view.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onImdbClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) 
        ) {
            when (state) {
                is DetailUiState.Loading -> LoadingView()
                is DetailUiState.Success -> DetailContent(
                    movie = state.movie,
                    onImdbClick = onImdbClick
                )
                is DetailUiState.Error -> {
                    ErrorView(
                        error = Top100UiError.Common(state.error), 
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}