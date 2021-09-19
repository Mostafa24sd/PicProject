package com.example.picproject.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.picproject.data.ConvertList
import com.example.picproject.data.UnsplashPhoto

@Database(
    entities = [UnsplashPhoto::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(ConvertList::class)
abstract class PhotoDB : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}