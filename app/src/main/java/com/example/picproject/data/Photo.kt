package com.example.picproject.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    val id: String,
    val width: Int,
    val height: Int,
    val color: String,
    val blur_hash: String,
    val description: String,
    val alt_description: String,
    val likes: Int,
    val views: Int,
    val downloads: Int,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser,
    val exif: Exif,
    val location: Location
) : Parcelable {

    @Parcelize
    data class UnsplashPhotoUrls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String
    ) : Parcelable

    @Parcelize
    data class UnsplashUser(
        val name: String,
        val username: String,
        val instagram_username: String,
        val profile_image: ProfileImage,
    ) : Parcelable {
        @Parcelize
        data class ProfileImage(
            val small: String
        ) : Parcelable
    }

    @Parcelize
    data class Exif(
        val make: String,
        val model: String,
        val exposure_time: String,
        val aperture: String,
        val focal_length: String,
        val iso: Int
    ) : Parcelable

    @Parcelize
    data class Location(
        val title: String,
        val name: String,
        val city: String,
        val country: String,
        val position: Position
    ) : Parcelable {

        @Parcelize
        data class Position(
            val latitude: Double,
            val longitude: Double
        ) : Parcelable
    }
}
