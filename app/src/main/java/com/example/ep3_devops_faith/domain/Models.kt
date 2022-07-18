package com.example.ep3_devops_faith.domain

import android.graphics.Bitmap

data class User(
    var Id: Long = 0L,
    var Auth0_key: String = "",
    var Name: String = "",
    var ProfileImageUrl: Bitmap? = null,
    var Role: String = ""
)
data class Post(
    var Id: Long = 0L,
    var Text: String = "",
    var Picture: Bitmap? = null,
    var Link: String = "",
    var UserId: Long = 0L
)
data class Comment(
    var Id: Long = 0L,
    val Message: String = "",
    val UserId: Long = 0L,
    val PostId: Long = 0L
)
