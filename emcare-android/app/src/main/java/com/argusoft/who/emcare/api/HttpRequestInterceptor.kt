package com.argusoft.who.emcare.api

import com.argusoft.who.emcare.model.AccessToken
import okhttp3.Interceptor
import okhttp3.Response


class HttpRequestInterceptor : Interceptor {

    companion object {
        var token:String = ""
    }

    //Note Add "http://localhost:8180/auth/" to Realm`s frontend url.
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val modifiedRequest = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(modifiedRequest)
    }

}