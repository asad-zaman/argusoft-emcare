package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.ui.common.model.Error
import com.argusoft.who.emcare.utils.extention.fromJson
import retrofit2.Response
import java.net.UnknownHostException

const val UNEXPECTED_INTERNAL_SERVER = "Unexpected internal server error."

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
//            400 -> ApiResponse.ServerError("Invalid syntax for this request was provided.")
            400, 401 -> {
                response.errorBody()?.string()?.fromJson<Error>()?.let {
                    ApiResponse.ApiError(it.errorDescription ?: it.errorMessage ?: "The application has encountered an unknown error.")
                } ?: ApiResponse.UnauthorizedAccess("You are unauthorized to access the requested resource. Please log in.")
            }
            404 -> ApiResponse.ServerError("We could not find the resource you requested. Please refer to the documentation for the list of resources.")
            500 -> ApiResponse.ServerError(UNEXPECTED_INTERNAL_SERVER)
            else -> ApiResponse.ServerError(UNEXPECTED_INTERNAL_SERVER)
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
        when (exception) {
            is UnknownHostException -> ApiResponse.NoInternetConnection
            else -> ApiResponse.ServerError(UNEXPECTED_INTERNAL_SERVER)
        }
    }
}
