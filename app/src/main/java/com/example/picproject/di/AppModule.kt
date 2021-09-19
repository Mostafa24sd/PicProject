package com.example.picproject.di

import android.content.Context
import androidx.room.Room
import com.example.picproject.api.UnsplashApi
import com.example.picproject.db.PhotoDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiClient(): UnsplashApi {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(UnsplashApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UnsplashApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, PhotoDB::class.java, "photoDB.db").build()

    @Provides
    @Singleton
    fun providePhotoDao(photoDB: PhotoDB) = photoDB.photoDao()

}

