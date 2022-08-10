package com.example.ep3_devops_faith.repository

import com.example.ep3_devops_faith.network.RetrofitService

class MovieRepository constructor(private val retrofitService: RetrofitService) {

    fun getAllMovies() = retrofitService.getAllMovies()
}