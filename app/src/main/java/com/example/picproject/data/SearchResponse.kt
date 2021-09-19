package com.example.picproject.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)

class ConvertList {
    @TypeConverter
    fun restoreList(value: List<UnsplashResponse>): String {
        val gson = Gson()
        val type = object : TypeToken<List<UnsplashResponse>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun saveList(value: String): List<UnsplashResponse> {
        val gson = Gson()
        val type = object : TypeToken<List<UnsplashResponse>>() {}.type
        return gson.fromJson(value, type)
    }
}
