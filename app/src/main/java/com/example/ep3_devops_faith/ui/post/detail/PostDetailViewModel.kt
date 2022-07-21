package com.example.ep3_devops_faith.ui.post.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ep3_devops_faith.domain.Post

class PostDetailViewModel(post: Post, application: Application) :
    AndroidViewModel(application) {
    private val _selectedProperty = MutableLiveData<Post>()

    // The external LiveData for the SelectedProperty
    val selectedProperty: LiveData<Post>
        get() = _selectedProperty

    // Initialize the _selectedProperty MutableLiveData
    init {
        _selectedProperty.value = post
    }
}