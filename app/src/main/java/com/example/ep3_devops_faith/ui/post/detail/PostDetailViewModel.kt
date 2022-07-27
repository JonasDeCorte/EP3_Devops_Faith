package com.example.ep3_devops_faith.ui.post.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.repository.PostRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class PostDetailViewModel(post: Post, application: Application) :
    AndroidViewModel(application) {
    private val db = FaithDatabase.getInstance(application.applicationContext)
    private val repository = PostRepository(db)

    // The external LiveData for the SelectedProperty
    private val _selectedProperty = MutableLiveData<Post>()
    val selectedProperty: LiveData<Post>
        get() = _selectedProperty

    // Initialize the _selectedProperty MutableLiveData
    init {
        viewModelScope.launch {
            _selectedProperty.value = post
            Timber.i("post= $post")
        }
    }
}