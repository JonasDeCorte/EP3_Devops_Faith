package com.example.ep3_devops_faith.ui.post.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.login.CredentialsManager
import com.example.ep3_devops_faith.repository.CommentRepository
import com.example.ep3_devops_faith.repository.FavoriteRepository
import com.example.ep3_devops_faith.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class FavoritePostsOverviewViewModel(val database: FaithDatabase, app: Application) :
    AndroidViewModel(app) {
    private val db = FaithDatabase.getInstance(app.applicationContext)
    private val repository = PostRepository(db)
    private val favoriteRepository = FavoriteRepository(db)
    private val commentRepository = CommentRepository(db)
    private val _items: MutableLiveData<List<Post>> = MutableLiveData(listOf())
    val items: LiveData<List<Post>> = _items
    init {
        viewModelScope.launch {
            initFavs()
            Timber.i("items === ${items.value?.size}")
        }
    }
    // TODO somehow move this to repo level.
    private fun initFavs() {
        viewModelScope.launch {
            val count = getUserFavoritesCount()
            Timber.i("amount of favorites for current logged in user === $count")
            count.forEach {
                Timber.i("postId $it")
                val dbPost = repository.get(it) // returns DatabasePost
                Timber.i("DBPOST ASFLOW ==== $dbPost")
                // make it a domain model Post
                val post = Post(
                    Text = dbPost.Text,
                    UserId = dbPost.UserId,
                    UserEmail = dbPost.UserEmail,
                    Link = dbPost.Link,
                    Picture = dbPost.Picture,
                    Id = dbPost.Id,
                    Status = dbPost.Status
                )
                // add to LiveData list.
                _items.value = _items.value?.plus(post) ?: listOf(post)
            }
        }
    }

    fun saveStatus(post: Post) {
        Timber.i("Save Status")
        viewModelScope.launch {
            val commentsForUser = commentRepository.countForUser(post.Id,
                CredentialsManager.cachedUserProfile?.getId()!!)
            Timber.i("Comments for user = $commentsForUser")
            Timber.i("postStatus = ${post.Status}")
            if (post.Status == "NEW") {
                Timber.i("Save Status ${post.Status} == NEW")
                saveStatusWithRepository("READ", post)
            }
            if (commentsForUser > 0 && post.Status == "READ") {
                Timber.i("Save Status ${post.Status} == READ")
                saveStatusWithRepository("ANSWERED", post)
            }
        }
    }

    private suspend fun getUserFavoritesCount(): List<Long> {
        return favoriteRepository.countUserFav()
    }

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

    private val _event = MutableLiveData<Post?>()
    val event: LiveData<Post?>
        get() = _event

    fun favoriteClick(post: Post) {
        _event.value = post
    }

    fun eventDone() {
        _event.value = null
    }

    fun saveFavorite(post: Post) {
        viewModelScope.launch {
            saveFavoriteWithRepository(post)
        }
    }

    fun removeFavorite(post: Post) {
        viewModelScope.launch {
            removeFavoriteWithRepository(post)
        }
    }

    fun isFavorite(post: Post): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val returnedVal = isFavoriteWithRepository(post)
            Timber.i("returned value isfav =  $returnedVal")
            result.postValue(returnedVal)
        }
        return result
    }

    private suspend fun isFavoriteWithRepository(post: Post): Boolean {
        val fav = favoriteRepository.get(post.Id)
        Timber.i("fav = $fav")
        if (fav == null) {
            return false
        }
        return true
    }

    private suspend fun saveFavoriteWithRepository(post: Post) {
        withContext(Dispatchers.IO) {
            favoriteRepository.insert(post)
        }
    }

    private suspend fun removeFavoriteWithRepository(post: Post) {
        withContext(Dispatchers.IO) {
            favoriteRepository.delete(post)
        }
    }
    private suspend fun saveStatusWithRepository(status: String, post: Post) {
        withContext(Dispatchers.IO) {
            repository.update(status, post)
        }
    }
}
