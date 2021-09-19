package com.example.picproject.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.picproject.data.UnsplashPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photos ORDER BY auto_id DESC")
    fun getPhotos(): Flow<List<UnsplashPhoto>>

    @Query("SELECT * FROM photos WHERE id=:id")
    fun getByID(id: String): Flow<UnsplashPhoto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: UnsplashPhoto)

    @Query("DELETE FROM photos WHERE id=:id")
    suspend fun delete(id: String)
}