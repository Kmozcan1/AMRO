package com.abn.amro.core.network.interceptor

import com.abn.amro.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        val urlWithKey = originalHttpUrl.newBuilder()
            .addQueryParameter(name = API_KEY, value = BuildConfig.TMDB_API_KEY)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(urlWithKey)
            .build()

        return chain.proceed(newRequest)
    }
}

private const val API_KEY = "api_key"