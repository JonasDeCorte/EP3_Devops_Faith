package com.example.ep3_devops_faith.ui.post.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ep3_devops_faith.domain.Post

class PostDetailViewModelFactory(
    private val post: Post,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
            return PostDetailViewModel(post, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
