package com.abn.amro.core.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface AmroResult<out T> {
    data class Success<T>(val data: T) : AmroResult<T>
    data class Error(val exception: Throwable) : AmroResult<Nothing>
    data object Loading : AmroResult<Nothing>
}

fun <T> Flow<T>.asResult(): Flow<AmroResult<T>> {
    return this
        .map<T, AmroResult<T>> { AmroResult.Success<T>(data = it) }
        .onStart { emit(value = AmroResult.Loading) }
        .catch { emit(value = AmroResult.Error(exception = it)) }
}