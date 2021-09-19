package com.example.picproject.data.saved

import androidx.lifecycle.liveData
import com.example.picproject.api.UnsplashApi
import com.example.picproject.data.Resource
import com.example.picproject.data.UnsplashPhoto
import com.example.picproject.db.PhotoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetailRepository @Inject constructor(
    private val photoDao: PhotoDao,
    private val api: UnsplashApi
) {

    fun getPhotoByID(id: String) = liveData(context = Dispatchers.IO) {
        emit(Resource.Loading(null))
        try {
            emit(Resource.Success(data = api.getPhoto(id)))
        } catch (e: HttpException) {
            emit(Resource.Error(e.message()))
        }
    }

    fun getPhotoFromDB(id: String): Flow<UnsplashPhoto> {
        return photoDao.getByID(id)
    }

    suspend fun savePhoto(photo: UnsplashPhoto) {
        photoDao.insert(photo)
    }

    suspend fun removePhoto(id: String) {
        photoDao.delete(id)
    }
}