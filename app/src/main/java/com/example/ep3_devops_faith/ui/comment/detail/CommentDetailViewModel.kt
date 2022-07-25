package com.example.ep3_devops_faith.ui.comment.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.domain.Comment
import com.example.ep3_devops_faith.login.CredentialsManager
import com.example.ep3_devops_faith.repository.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentDetailViewModel(comment: Comment, application: Application) :
    AndroidViewModel(application) {
    private val _selectedProperty = MutableLiveData<Comment>()
    val updateEvent: LiveData<Boolean>
        get() = _updateEvent
    private val _updateEvent = MutableLiveData<Boolean>()
    val deleteEvent: LiveData<Boolean>
        get() = _deleteEvent
    private val _deleteEvent = MutableLiveData<Boolean>()
    val db = FaithDatabase.getInstance(application.applicationContext)
    val repository = CommentRepository(db)
    private val _visibleBool: Boolean =
        CredentialsManager.cachedUserProfile!!.getId().equals(comment.UserId)
    val visibleBool: Boolean
        get() = _visibleBool

    // The external LiveData for the SelectedProperty
    val selectedProperty: LiveData<Comment>
        get() = _selectedProperty

    // Initialize the _selectedProperty MutableLiveData
    init {
        _updateEvent.value = false
        _deleteEvent.value = false
        _selectedProperty.value = comment
    }

    fun updateCommentClick() {
        _updateEvent.value = true
    }

    fun updateEventDone() {
        _updateEvent.value = false
    }

    fun updateComment(comment: Comment) {
        viewModelScope.launch {
            saveCommentWithRepository(comment)
        }
    }

    private suspend fun saveCommentWithRepository(newComment: Comment) {
        withContext(Dispatchers.IO) {
            repository.update(newComment)
        }
    }

    fun deleteCommentClick() {
        _deleteEvent.value = true
    }

    fun deleteEventDone() {
        _deleteEvent.value = false
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            deleteCommentWithRepository(comment)
        }
    }

    private suspend fun deleteCommentWithRepository(comment: Comment) {
        withContext(Dispatchers.IO) {
            repository.delete(comment)
        }
    }
}