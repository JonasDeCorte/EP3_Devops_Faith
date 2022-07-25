package com.example.ep3_devops_faith.ui.post.create

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.post.PostDatabaseDao
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPostViewModel(val database: PostDatabaseDao, application: Application) :
    AndroidViewModel(application) {
    private val db = FaithDatabase.getInstance(application.applicationContext)
    private val repository = PostRepository(db)
    private val _saveEvent = MutableLiveData<Boolean>()
    val saveEvent: LiveData<Boolean>
        get() = _saveEvent

    init {
        _saveEvent.value = false
    }

    fun savePostClick() {
        _saveEvent.value = true
    }

    fun saveEventDone() {
        _saveEvent.value = false
    }

    fun savePost(text: String, avatar: Bitmap?, link: String?, userId: String, userEmail: String) {
        viewModelScope.launch {
            val post = Post()
            post.Text = text
            post.Picture = avatar
            post.Link = link!!
            post.UserId = userId
            post.UserEmail = userEmail
            savePostWithRepository(post)
        }
    }

    private suspend fun savePostWithRepository(newPost: Post) {
        withContext(Dispatchers.IO) {
            repository.insert(newPost)
        }
    }
}