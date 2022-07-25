package com.example.ep3_devops_faith.ui.post.favorites

import android.app.Application
import androidx.lifecycle.*
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.post.PostDatabaseDao
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.login.CredentialsManager
import com.example.ep3_devops_faith.repository.FavoriteRepository
import com.example.ep3_devops_faith.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class FavoritePostsOverviewViewModel(val database: PostDatabaseDao, app: Application) :
    AndroidViewModel(app) {
    private val db = FaithDatabase.getInstance(app.applicationContext)
    private val repository = PostRepository(db)
    private val favoriteRepository = FavoriteRepository(db)

    private var _items: MutableLiveData<List<Post>> = MutableLiveData(listOf())
    val items: LiveData<List<Post>> = _items

    init {
        initFavs()
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
                        Id = dbPost.Id
                    )
                    // add to LiveData list.
                    _items.value = _items.value?.plus(post) ?: listOf(post)
                }
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

    fun FavoriteClick(post: Post) {
        _Event.value = post
    }

    fun EventDone() {
        _Event.value = null
    }

    fun saveFavorite(post: Post) {
        viewModelScope.launch {
            saveFavoriteWithRepository(post)
            initFavs()
        }
    }

    fun removeFavorite(post: Post) {
        viewModelScope.launch {
            removeFavoriteWithRepository(post)
            initFavs()
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
}
