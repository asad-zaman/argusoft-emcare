package com.argusoft.who.emcare.oldstruct.api

import com.argusoft.who.emcare.oldstruct.model.AccessToken
import com.argusoft.who.emcare.oldstruct.static.CompanionValues
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface KeycloakLoginService {

    @FormUrlEncoded
    @POST("/auth/realms/emcare_demo/protocol/openid-connect/token")
    fun getAccessToken(
        @Field("client_id") client_id: String,
        @Field("grant_type") grant_type: String,
        @Field("client_secret") client_secret: String,
        @Field("scope") scope: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<AccessToken>

    companion object {

        fun create(): KeycloakLoginService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder().addInterceptor(logger).build()
            return Retrofit.Builder()
                .baseUrl(CompanionValues.KEYCLOAK_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KeycloakLoginService::class.java)
        }
    }
}