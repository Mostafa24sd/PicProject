package com.example.picproject.data.topic

import androidx.lifecycle.liveData
import com.example.picproject.api.UnsplashApi
import com.example.picproject.data.Resource
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(private val unsplashApi: UnsplashApi) {
    fun getTopics() = liveData(context = Dispatchers.IO) {
        emit(Resource.Loading(null))

        try {
            emit(Resource.Success(data = unsplashApi.topics()))
        } catch (e: HttpException) {
            emit(Resource.Error(e.message()))
        }
    }
}