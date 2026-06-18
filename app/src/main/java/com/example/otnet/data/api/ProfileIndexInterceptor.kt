package com.example.otnet.data.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds `X-Profile-Index: <n>` to every request. Without this the server falls
 * back to profile 0, which bypasses rating gates set on the active profile.
 */
class ProfileIndexInterceptor(
    private val indexProvider: () -> Int,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("X-Profile-Index", indexProvider().coerceAtLeast(0).toString())
            .build()
        return chain.proceed(request)
    }
}
