package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.ui.common.model.Album

interface Api {

    suspend fun getRepository(request: Any): ApiResponse<List<Album>>
}
