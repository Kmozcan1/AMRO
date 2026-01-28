package com.abn.amro.movies.ui.feature.top100.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abn.amro.core.common.mapper.toAmroError
import com.abn.amro.core.common.result.AmroResult
import com.abn.amro.movies.domain.model.Movie
import com.abn.amro.movies.domain.repository.MovieRepository
import com.abn.amro.movies.ui.feature.top100.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class Top100ViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _selectedGenreId = MutableStateFlow<Int?>(null)
    private val _sortConfig = MutableStateFlow(Top100SortConfig())

    private val _retryTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _effects = Channel<Top100UiEffect>(capacity = Channel.BUFFERED)
    val effects: Flow<Top100UiEffect> = _effects.receiveAsFlow()

    init {
        _retryTrigger.tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<Top100UiState> = _retryTrigger.flatMapLatest {
        combine(
            repository.getTop100Movies(),
            repository.getMovieGenres(),
            _selectedGenreId,
            _sortConfig
        ) { moviesResult, genresResult, selectedGenreId, sortConfig ->

            val error = (moviesResult as? AmroResult.Error)?.exception?.toAmroError()
                ?: (genresResult as? AmroResult.Error)?.exception?.toAmroError()

            if (error != null) {
                return@combine Top100UiState.Error(error)
            }

            if (moviesResult !is AmroResult.Success || genresResult !is AmroResult.Success) {
                return@combine Top100UiState.Loading
            }

            val allMovies = moviesResult.data
            val genres = genresResult.data
            val genreMap = genres.associate { it.id to it.name }

            val filteredMovies = if (selectedGenreId == null) {
                allMovies
            } else {
                allMovies.filter { it.genreIds.contains(selectedGenreId) }
            }

            val sortedMovies = sortMovies(filteredMovies, sortConfig)

            Top100UiState.Success(
                movies = sortedMovies.map { it.toUiModel(genreMap) },
                availableGenres = genres,
                selectedGenreId = selectedGenreId,
                sortConfig = sortConfig
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Top100UiState.Loading
    )

    private fun sortMovies(movies: List<Movie>, config: Top100SortConfig): List<Movie> {
        val comparator = when (config.type) {
            SortType.POPULARITY -> compareBy<Movie> { it.popularity }
            SortType.TITLE -> compareBy { it.title }
            SortType.RELEASE_DATE -> compareBy { it.releaseDate ?: "" }
        }

        return if (config.order == SortOrder.Ascending) {
            movies.sortedWith(comparator)
        } else {
            movies.sortedWith(comparator.reversed())
        }
    }

    fun onEvent(event: Top100UiEvent) {
        when (event) {
            is Top100UiEvent.OnGenreSelected -> _selectedGenreId.update { event.genreId }
            is Top100UiEvent.OnSortConfigChanged -> _sortConfig.update { event.config }
            is Top100UiEvent.OnRetry -> _retryTrigger.tryEmit(Unit)
            is Top100UiEvent.OnMovieClicked -> {
                _effects.trySend(Top100UiEffect.NavigateToDetail(event.movieId, event.color))
            }
        }
    }
}