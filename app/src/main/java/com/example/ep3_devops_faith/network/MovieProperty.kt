package com.example.ep3_devops_faith.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoviePropertyList(
    @Json(name = "Search") val movieList: List<MovieProperty>
)

@JsonClass(generateAdapter = true)
data class MovieProperty(
    @Json(name = "Title") val title: String,
    @Json(name = "Poster") val poster: String,
    val imdbID: String,
    @Json(name = "Year") val year: String
)