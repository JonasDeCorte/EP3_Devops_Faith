package com.example.ep3_devops_faith.domain

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    var Id: Long = 0L,
    var Text: String = "",
    var Picture: Bitmap? = null,
    var Link: String = "",
    var UserId: String = "",
    var UserEmail: String = "",
    var Status: String = ""
) : Parcelable
@Parcelize
data class Comment(
    var Id: Long = 0L,
    var Message: String = "",
    var UserId: String = "",
    var PostId: Long = 0L,
    var UserEmail: String = ""
) : Parcelable
data class Favorite(
    var Id: Long = 0L,
    var UserId: String = "",
    var PostId: Long = 0L
)
