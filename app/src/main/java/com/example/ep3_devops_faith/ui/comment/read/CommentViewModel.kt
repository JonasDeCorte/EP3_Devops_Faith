package com.example.ep3_devops_faith.ui.comment.read

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.comment.CommentDatabaseDao
import com.example.ep3_devops_faith.domain.Comment
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.repository.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentViewModel(post: Post, val database: CommentDatabaseDao, app: Application) :
    AndroidViewModel(app) {
    private val _saveEvent = MutableLiveData<Boolean>()
    val saveEvent: LiveData<Boolean>
        get() = _saveEvent
    val db = FaithDatabase.getInstance(app.applicationContext)
    val repository = CommentRepository(db)
    var comments: LiveData<List<Comment>>? = null

    init {
        _saveEvent.value = false
        viewModelScope.launch {
            comments = repository.allCommentForPost(post.Id)
        }
    }

    fun saveCommentClick() {
        _saveEvent.value = true
    }

    fun saveEventDone() {
        _saveEvent.value = false
    }

    fun saveComment(message: String, postId: Long, userId: String) {
        viewModelScope.launch {
            val comment = Comment()
            comment.Message = message
            comment.PostId = postId
            comment.UserId = userId
            saveCommentWithRepository(comment)
        }
    }

    suspend fun saveCommentWithRepository(newComment: Comment) {
        withContext(Dispatchers.IO) {
            repository.insert(newComment)
        }
    }
}