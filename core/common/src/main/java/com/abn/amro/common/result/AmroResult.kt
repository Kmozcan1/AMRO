package com.abn.amro.common.result

sealed interface AmroResult<out T> {
    data class Success<T>(val data: T) : AmroResult<T>
    data class Error(val exception: Throwable? = null, val message: String? = null) :
        AmroResult<Nothing>

    data object Loading : AmroResult<Nothing>
}