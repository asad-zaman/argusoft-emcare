package com.argusoft.who.emcare.data.remote

sealed class ApiResponse<out T> {

    data class Success<out T>(val data: T?, val successMessage: String? = null, val isRequiredClear: Boolean = false) : ApiResponse<T>()

    data class InProgress(val total: Int = 0, val completed: Int = 0, val progressCount : Int = 0) : ApiResponse<Nothing>()

    data class Loading(val isRefresh: Boolean = false, val isLoadMore: Boolean = false) : ApiResponse<Nothing>()

    data class ApiError(val apiErrorMessage: String? = null, val apiErrorMessageResId: Int? = null) : ApiResponse<Nothing>()

    data class ServerError(val errorMessage: String) : ApiResponse<Nothing>()

    data class UnauthorizedAccess(val errorMessage: String) : ApiResponse<Nothing>()

    object NoInternetConnection : ApiResponse<Nothing>()
}
