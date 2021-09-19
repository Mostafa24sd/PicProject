package com.example.picproject.data.saved

import com.example.picproject.data.UnsplashPhoto
import com.example.picproject.db.PhotoDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedRepository @Inject constructor(private val photoDao: PhotoDao) {

    val photos: Flow<List<UnsplashPhoto>> = photoDao.getPhotos()
}