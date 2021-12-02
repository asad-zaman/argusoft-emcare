package com.argusoft.who.emcare.data.local.database

import com.argusoft.who.emcare.ui.common.model.Album

interface Database {
    suspend fun getAll(): List<Album>

    suspend fun addAll(list: List<Album>)
}