package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.ui.common.model.User
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface KeyCloakApiService {

    @FormUrlEncoded
    @POST("token")
    suspend fun getAccessToken(
        @FieldMap filedMap: Map<String, String>
    ): Response<User>
}
