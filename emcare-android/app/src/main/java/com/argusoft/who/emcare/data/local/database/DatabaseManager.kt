package com.argusoft.who.emcare.data.local.database

class DatabaseManager(roomDatabase: RoomDatabase) : Database {

    private val dao = roomDatabase.dao()


}