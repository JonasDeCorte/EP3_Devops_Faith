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
    private val db = FaithDatabase.getInstance(app.applicationContext)
    private val repository = CommentRepository(db)
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

    fun saveComment(message: String, postId: Long, userId: String, userEmail: String) {
        viewModelScope.launch {
            val comment = Comment()
            comment.Message = message
            comment.PostId = postId
            comment.UserId = userId
            comment.UserEmail = userEmail
            saveCommentWithRepository(comment)
        }
    }

    private suspend fun saveCommentWithRepository(newComment: Comment) {
        withContext(Dispatchers.IO) {
            repository.insert(newComment)
        }
    }

    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<Comment?>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedProperty: MutableLiveData<Comment?>
        get() = _navigateToSelectedProperty

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param comment The [Comment] that was clicked on.
     */
    fun displayPropertyDetails(comment: Comment) {
        _navigateToSelectedProperty.value = comment
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }
}