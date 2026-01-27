package com.abn.amro.movies.ui.feature.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abn.amro.core.common.di.TmdbImageBaseUrl
import com.abn.amro.core.common.mapper.toAmroError
import com.abn.amro.core.common.result.AmroResult
import com.abn.amro.movies.domain.repository.MovieRepository
import com.abn.amro.movies.ui.mapper.toDetailUiModel
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
    @param:TmdbImageBaseUrl private val imageBaseUrl: String
) : ViewModel() {

    private val movieId: Long = checkNotNull(
        savedStateHandle[MovieDetailDestination.ARG_MOVIE_ID]
    ) { "Movie ID is required for Detail Screen" }.toString().toLong()

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _effects = Channel<DetailUiEffect>(Channel.BUFFERED)
    val effects: Flow<DetailUiEffect> = _effects.receiveAsFlow()

    init {
        loadMovie()
    }

    fun onEvent(event: DetailUiEvent) {
        when (event) {
            DetailUiEvent.OnBackClicked -> _effects.trySend(DetailUiEffect.NavigateBack)
            DetailUiEvent.OnRetry -> loadMovie()
            DetailUiEvent.OnImdbClicked -> {
                val state = _uiState.value
                if (state is DetailUiState.Success && state.movie.imdbUrl != null) {
                    _effects.trySend(DetailUiEffect.OpenUrl(state.movie.imdbUrl))
                }
            }
        }
    }

    private fun loadMovie() {
        viewModelScope.launch {
            _uiState.update { DetailUiState.Loading }
            
            repository.getMovieDetails(movieId).collect { result ->
                when (result) {
                    is AmroResult.Loading -> _uiState.update { DetailUiState.Loading }
                    is AmroResult.Success -> {
                        val uiModel = result.data.toDetailUiModel(imageBaseUrl)
                        _uiState.update { DetailUiState.Success(uiModel) }
                    }
                    is AmroResult.Error -> {
                        _uiState.update { DetailUiState.Error(result.exception.toAmroError()) }
                    }
                }
            }
        }
    }
}