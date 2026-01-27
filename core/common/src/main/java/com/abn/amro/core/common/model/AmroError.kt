package com.abn.amro.core.common.model

/**
 * Universal error types for the entire application.
 */
sealed interface AmroError {
    data object Network : AmroError
    data object Server : AmroError
    data class Unknown(val debugMessage: String? = null) : AmroError
}