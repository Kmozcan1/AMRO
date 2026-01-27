package com.abn.amro.core.common.mapper

import com.abn.amro.core.common.model.AmroError
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toAmroError(): AmroError {
    return when (this) {
        is IOException -> AmroError.Network
        is HttpException -> AmroError.Server
        else -> AmroError.Unknown(this.localizedMessage ?: "Unknown Error")
    }
}