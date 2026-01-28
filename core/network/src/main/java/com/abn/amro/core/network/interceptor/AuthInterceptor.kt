package com.abn.amro.core.network.interceptor

import com.abn.amro.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {
    private companion object {
        const val API_KEY_QUERY_PARAM = "api_key"
        const val LANGUAGE_QUERY_PARAM = "language"
        const val DEFAULT_LANGUAGE_VALUE = "en-US"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        val urlWithKey = originalHttpUrl.newBuilder()
            .addQueryParameter(name = API_KEY_QUERY_PARAM, value = BuildConfig.TMDB_API_KEY)
            .addQueryParameter(name = LANGUAGE_QUERY_PARAM, value = DEFAULT_LANGUAGE_VALUE)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(urlWithKey)
            .build()

        return chain.proceed(newRequest)
    }
}