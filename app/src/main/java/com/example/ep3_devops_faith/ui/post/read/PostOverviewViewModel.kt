package com.example.ep3_devops_faith.ui.post.read

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.post.PostDatabaseDao
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.repository.PostRepository

class PostOverviewViewModel(val database: PostDatabaseDao, app: Application) :
    AndroidViewModel(app) {
    private val db = FaithDatabase.getInstance(app.applicationContext)
    private val repository = PostRepository(db)
    val posts = repository.allPosts

    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<Post?>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedProperty: MutableLiveData<Post?>
        get() = _navigateToSelectedProperty

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param post The [Post] that was clicked on.
     */
    fun displayPropertyDetails(post: Post) {
        _navigateToSelectedProperty.value = post
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }
}