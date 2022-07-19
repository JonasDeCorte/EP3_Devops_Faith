package com.example.ep3_devops_faith.domain

import android.graphics.Bitmap

data class Post(
    var Id: Long = 0L,
    var Text: String = "",
    var Picture: Bitmap? = null,
    var Link: String = "",
    var UserId: String = ""
)
data class Comment(
    var Id: Long = 0L,
    val Message: String = "",
    val UserId: Long = 0L,
    val PostId: Long = 0L
)
