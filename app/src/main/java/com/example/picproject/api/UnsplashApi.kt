package com.example.picproject.api

import com.example.picproject.data.Photo
import com.example.picproject.data.Topic
import com.example.picproject.data.UnsplashPhoto
import com.example.picproject.data.UnsplashResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApi {
    companion object {
        const val BASE_URL = "https://api.unsplash.com/"
        const val ACCESS_KEY2 =
            "DJLJ3W5ODUXKU2HbB6JP4aDAJZeP1W5yCFN_IgdgCjo"
        const val ACCESS_KEY4 =
            "jt1A1Zd0a5K1X0nR5bbnkOj0DwFqs6l6aOKkHeAkLcA"
        const val ACCESS_KEY3 =
            "Tp8IPhiMCiH8SAdgdF92l-2eTxc8gLJvXdPrKsINTc"
        const val ACCESS_KEY =
            "ece4fa5efbe76bcec26bf982d7b7ef8b27bd80d832dbe8a40bd954448fc4320a"
    }

    @GET("search/photos")
    @Headers("Authorization: Client-ID $ACCESS_KEY")
    suspend fun searchPhoto(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") sortBy: String,
    ): UnsplashResponse

    @GET("photos")
    @Headers("Authorization: Client-ID $ACCESS_KEY")
    suspend fun listPhoto(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") orderBy: String = "popular"
    ): List<UnsplashPhoto>

    @GET("topics")
    @Headers("Authorization: Client-ID $ACCESS_KEY")
    suspend fun topics(
        @Query("per_page") perPage: Int = 50,
        @Query("order_by") orderBy: String = "featured"
    ): List<Topic>

    @GET("topics/{id}/photos")
    @Headers("Authorization: Client-ID $ACCESS_KEY")
    suspend fun topicsPhotos(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") sortBy: String
    ): List<UnsplashPhoto>

    @GET("users/{username}/photos")
    @Headers("Authorization: Client-ID $ACCESS_KEY")
    suspend fun userPhotos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") sortBy: String
    ): List<UnsplashPhoto>

    @GET("photos/{id}")
    @Headers("Authorization: Client-ID $ACCESS_KEY")
    suspend fun getPhoto(
        @Path("id") id: String
    ): Photo
}