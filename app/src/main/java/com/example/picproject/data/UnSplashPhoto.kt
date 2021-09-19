package com.example.picproject.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "photos")
data class UnsplashPhoto(
    @PrimaryKey(autoGenerate = true)
    val auto_id: Int,
    val id: String,
    val width: Int,
    val height: Int,
    val description: String?,
    @Embedded(prefix = "urls_")
    val urls: UnsplashPhotoUrls,
    @Embedded(prefix = "user_")
    val user: UnsplashUser
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
        val instagram_username: String?,
        @Embedded(prefix = "profile_")
        val profile_image: ProfileImage,
    ) : Parcelable {
        @Parcelize
        data class ProfileImage(
            val small: String
        ) : Parcelable
    }
}