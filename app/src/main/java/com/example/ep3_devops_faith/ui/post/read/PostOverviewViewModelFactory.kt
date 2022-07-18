package com.example.ep3_devops_faith.ui.post.read

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ep3_devops_faith.database.post.PostDatabaseDao

class PostOverviewViewModelFactory(
    private val dataSource: PostDatabaseDao,
    private val application: Application,
    private val adapter: PostAdapter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostOverviewViewModel::class.java)) {
            return PostOverviewViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}