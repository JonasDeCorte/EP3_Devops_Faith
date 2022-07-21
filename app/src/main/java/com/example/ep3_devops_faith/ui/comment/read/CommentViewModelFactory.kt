package com.example.ep3_devops_faith.ui.comment.read

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ep3_devops_faith.database.comment.CommentDatabaseDao
import com.example.ep3_devops_faith.domain.Post

class CommentViewModelFactory(
    private val post: Post,
    private val database: CommentDatabaseDao,
    private val app: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            return CommentViewModel(post, database, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}