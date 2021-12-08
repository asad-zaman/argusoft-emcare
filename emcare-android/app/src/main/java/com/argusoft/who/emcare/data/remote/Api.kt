package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.ui.common.model.User

interface Api {

    suspend fun login(requestMap: Map<String, String>) : ApiResponse<User>
}
