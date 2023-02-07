package com.argusoft.who.emcare.data.remote

import okhttp3.RequestBody
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import retrofit2.http.*

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

}
