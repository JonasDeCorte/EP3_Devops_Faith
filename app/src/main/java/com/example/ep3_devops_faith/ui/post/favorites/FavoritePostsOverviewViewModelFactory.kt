package com.example.ep3_devops_faith.ui.post.favorites

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ep3_devops_faith.database.post.PostDatabaseDao

class FavoritePostsOverviewViewModelFactory(
    private val dataSource: PostDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritePostsOverviewViewModel::class.java)) {
            return FavoritePostsOverviewViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}