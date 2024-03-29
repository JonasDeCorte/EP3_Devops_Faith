package com.example.ep3_devops_faith.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.post.DatabasePost
import com.example.ep3_devops_faith.database.post.asDomainmodel
import com.example.ep3_devops_faith.domain.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PostRepository(private val faithDatabase: FaithDatabase) {

    val allPosts: LiveData<List<Post>> =
        Transformations.map(faithDatabase.postDatabaseDao.getAllEntries()) {
            it.asDomainmodel()
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