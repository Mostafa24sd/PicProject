package com.example.picproject.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Topic(
    val id: String,
    val title: String,
    val total_photos: Int,
    val cover_photo: CoverPhoto,
) : Parcelable {

    @Parcelize
    data class CoverPhoto(
        val id: String,
        val width: Int,
        val height: Int,
        val color: String,
        val urls: CoverUrls,
    ) : Parcelable {

        @Parcelize
        data class CoverUrls(
            val raw: String,
            val full: String,
            val regular: String,
            val small: String,
            val thumb: String,
        ) : Parcelable
    }
}