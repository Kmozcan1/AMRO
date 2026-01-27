package com.abn.amro.movies.ui.feature.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abn.amro.core.common.mapper.toAmroError
import com.abn.amro.core.common.result.AmroResult
import com.abn.amro.movies.domain.repository.MovieRepository
import com.abn.amro.movies.ui.feature.detail.mapper.MovieDetailUiMapper
import com.abn.amro.movies.ui.navigation.MovieDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MovieRepository,
    private val mapper: MovieDetailUiMapper
) : ViewModel() {

    private val movieId: Long =
        savedStateHandle.get<String>(MovieDetailDestination.ARG_MOVIE_ID)?.toLongOrNull()
            ?: savedStateHandle.get<Long>(MovieDetailDestination.ARG_MOVIE_ID)
            ?: error("Movie ID is required for Detail Screen")

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _effects = Channel<DetailUiEffect>(Channel.BUFFERED)
    val effects: Flow<DetailUiEffect> = _effects.receiveAsFlow()

    private var loadJob: kotlinx.coroutines.Job? = null

    init {
        loadMovie()
    }

    fun onEvent(event: DetailUiEvent) {
        when (event) {
            DetailUiEvent.OnBackClicked ->
                _effects.trySend(DetailUiEffect.NavigateBack)

            DetailUiEvent.OnRetry ->
                loadMovie()

            DetailUiEvent.OnImdbClicked -> {
                val movie = (uiState.value as? DetailUiState.Success)?.movie
                val url = movie?.imdbUrl
                if (!url.isNullOrBlank()) {
                    _effects.trySend(DetailUiEffect.OpenUrl(url))
                }
            }
        }
    }

    private fun loadMovie() {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.update { DetailUiState.Loading }

            repository.getMovieDetails(movieId).collect { result ->
                when (result) {
                    is AmroResult.Loading ->
                        _uiState.update { DetailUiState.Loading }

                    is AmroResult.Success -> {
                        val uiModel = mapper.map(result.data)
                        _uiState.update { DetailUiState.Success(uiModel) }
                    }

                    is AmroResult.Error ->
                        _uiState.update { DetailUiState.Error(result.exception.toAmroError()) }
                }
            }
        }
    }
}
