package com.argusoft.who.emcare.data.remote.fhirService

import ca.uhn.fhir.parser.IParser
import com.argusoft.who.emcare.oldstruct.api.FhirConverterFactory
import com.argusoft.who.emcare.oldstruct.static.CompanionValues
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

/** hapi.fhir.org API communication via Retrofit */
interface ServerFhirService {

    @GET
    suspend fun getResource(@Url url: String): Bundle

    @PUT("{type}/{id}")
    suspend fun insertResource(
        @Path("type") type: String,
        @Path("id") id: String,
        @Body body: RequestBody
    ): Resource

    @PATCH("{type}/{id}")
    suspend fun updateResource(
        @Path("type") type: String,
        @Path("id") id: String,
        @Body body: RequestBody
    ): OperationOutcome

    @DELETE("{type}/{id}")
    suspend fun deleteResource(@Path("type") type: String, @Path("id") id: String): OperationOutcome

    companion object {

        fun create(parser: IParser): ServerFhirService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpRequestInterceptor())
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(CompanionValues.LOCAL_BASE_URL)
                .client(client)
                .addConverterFactory(FhirConverterFactory(parser))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ServerFhirService::class.java)
        }
    }
}
