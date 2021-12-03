package com.argusoft.who.emcare.api

import okhttp3.Interceptor
import okhttp3.Response

class HttpRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val modifiedRequest = request.newBuilder()
            .header("Authorisation", "TOKEN_GOES_HERE").build()
        return chain.proceed(modifiedRequest)
    }
}