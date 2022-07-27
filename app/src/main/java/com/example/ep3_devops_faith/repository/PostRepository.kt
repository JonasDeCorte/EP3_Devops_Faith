package com.example.ep3_devops_faith.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.post.DatabasePost
import com.example.ep3_devops_faith.database.post.asDomainmodel
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.login.CredentialsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PostRepository(private val faithDatabase: FaithDatabase) {

    val allPosts: LiveData<List<Post>> =
        Transformations.map(faithDatabase.postDatabaseDao.getAllEntries()) {
            it.asDomainmodel()
        }
    var postIds: LiveData<List<Long>> =
        faithDatabase.favoriteDatabaseDao.getUserFavorites(CredentialsManager.cachedUserProfile?.getId()!!)

    suspend fun getUserFavs(): LiveData<List<Long>> {
        return withContext(Dispatchers.IO) {
            faithDatabase.favoriteDatabaseDao.getUserFavorites(CredentialsManager.cachedUserProfile?.getId()!!)
        }
    }

    suspend fun update(status: String, post: Post) {
        return withContext(Dispatchers.IO) {
            Timber.i("Update called ")
            var dbPost = faithDatabase.postDatabaseDao.get(post.Id)
            Timber.i("dbpost = $dbPost")
            dbPost.Status = status
            Timber.i("dbpost after status change = $dbPost")
            faithDatabase.postDatabaseDao.update(dbPost)
        }
    }
    /*suspend fun getFavos(): LiveData<List<Post>> {
        val _items: MutableLiveData<List<Post>> = MutableLiveData(listOf())
        val items: LiveData<List<Post>> = _items
        val postIds: List<Long>
        var dbPost: DatabasePost

        withContext(Dispatchers.IO) {
            postIds = getUserFavs()
        }
        for (id in postIds) {
            withContext(Dispatchers.IO) {
                dbPost = faithDatabase.postDatabaseDao.get(id)
            }
            val post = Post(
                Text = dbPost.Text,
                UserId = dbPost.UserId,
                UserEmail = dbPost.UserEmail,
                Link = dbPost.Link,
                Picture = dbPost.Picture,
                Id = dbPost.Id
            )
            _items.value = _items.value?.plus(post) ?: listOf(post)
        }
        Timber.i("items= " + items.value!!.size)
        return items
    }*/

    // Database call
    suspend fun insert(post: Post) {
        // switch context to IO thread
        withContext(Dispatchers.IO) {
            Timber.i("insert post called")
            val newDatabasePost = DatabasePost(
                Text = post.Text,
                Picture = post.Picture,
                Link = post.Link,
                UserId = post.UserId,
                UserEmail = post.UserEmail,
                Status = post.Status
            )
            Timber.i("newDatabasePost $newDatabasePost")
            faithDatabase.postDatabaseDao.insert(newDatabasePost)
            Timber.i("insert post success")
        }
    }

    suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            faithDatabase.postDatabaseDao.getDataCount()
        }
    }

    suspend fun get(id: Long): DatabasePost {
        return withContext(Dispatchers.IO) {
            faithDatabase.postDatabaseDao.get(id)
        }
    }

    suspend fun delete(post: Post) {
        return withContext(Dispatchers.IO) {
            val databasePost = faithDatabase.postDatabaseDao.get(post.Id)
            faithDatabase.postDatabaseDao.delete(databasePost)
        }
    }
}