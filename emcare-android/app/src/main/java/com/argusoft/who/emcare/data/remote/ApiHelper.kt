package com.argusoft.who.emcare.data.remote

import retrofit2.Response
import java.net.UnknownHostException

inline fun <T> executeApiHelper(responseMethod: () -> Response<T>): ApiResponse<T> {
    return try {
        val response = responseMethod.invoke()
        when (response.code()) {
            in 200..300 -> {
                val responseBody = response.body()
                if (responseBody != null) {
                    ApiResponse.Success(responseBody)
                } else ApiResponse.ServerError("The application has encountered an unknown error.")
            }
            400 -> ApiResponse.ServerError("Invalid syntax for this request was provided.")
            401 -> ApiResponse.UnauthorizedAccess("You are unauthorized to access the requested resource. Please log in.")
            404 -> ApiResponse.ServerError("We could not find the resource you requested. Please refer to the documentation for the list of resources.")
            500 -> ApiResponse.ServerError("Unexpected internal server error.")
            else -> ApiResponse.ServerError("Unexpected internal server error.")
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
        when (exception) {
            is UnknownHostException -> ApiResponse.NoInternetConnection
            else -> ApiResponse.ServerError("Unexpected internal server error.")
        }
    }
}
