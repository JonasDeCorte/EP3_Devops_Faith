package com.example.ep3_devops_faith.ui.post.favorites

import android.app.Application
import androidx.lifecycle.*
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
            Timber.i(items!!.value.toString())
        }
    }

    // TODO somehow move this to repo level.
    private fun initFavs() {
        viewModelScope.launch {
            val favos =
                getUserFavorites(CredentialsManager.cachedUserProfile?.getId()!!) // returns a list of postId's
            Timber.i("Favos = " + favos.value?.size.toString())
            favos.asFlow().collect {
                it.forEach { postId ->
                    val dbPost = repository.get(postId) // returns DatabasePost
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
    }

    fun saveStatus(post: Post) {
        Timber.i("Save Status")
        viewModelScope.launch {
            var commentsForUser = commentRepository.countForUser(post.Id,
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
    fun getUserFavorites(userName: String): LiveData<List<Long>> {
        return favoriteRepository.getUserFavorites(userName)
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

    private val _Event = MutableLiveData<Post?>()
    val event: LiveData<Post?>
        get() = _Event

    fun favoriteClick(post: Post) {
        _Event.value = post
    }

    fun EventDone() {
        _Event.value = null
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
            Timber.i("returned value isfav =  " + returnedVal.toString())
            result.postValue(returnedVal)
        }
        return result
    }

    suspend fun isFavoriteWithRepository(post: Post): Boolean {
        val fav = favoriteRepository.get(post.Id)
        Timber.i("fav = " + fav.toString())
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
