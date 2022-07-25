package com.example.ep3_devops_faith.ui.comment.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ep3_devops_faith.domain.Comment

class CommentDetailViewModelFactory(
    private val comment: Comment,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentDetailViewModel::class.java)) {
            return CommentDetailViewModel(comment, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
