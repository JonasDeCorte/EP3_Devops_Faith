package com.example.ep3_devops_faith.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.post.DatabasePost
import com.example.ep3_devops_faith.database.post.asDomainmodel
import com.example.ep3_devops_faith.domain.Post
import timber.log.Timber

class PostRepository(private val faithDatabase: FaithDatabase) {

    val allPosts: LiveData<List<Post>> =
        Transformations.map(faithDatabase.postDatabaseDao.getAllEntries()) {
            it.asDomainmodel()
        }

    suspend fun insert(post: Post) {
        Timber.i("insert post called")
        val newDatabasePost = DatabasePost(
            Text = post.Text,
            Picture = post.Picture,
            Link = post.Link,
            UserId = ""
            // UserId = post.UserId
        )
        faithDatabase.postDatabaseDao.insert(newDatabasePost)
        Timber.i("insert post success")
    }

    suspend fun count(): Int {
        return faithDatabase.postDatabaseDao.getDataCount()
    }

    suspend fun delete(post: Post) {
        val databasePost = faithDatabase.postDatabaseDao.get(post.Id)
        faithDatabase.postDatabaseDao.delete(databasePost)
    }

    // suspend fun update(post: Post) {
//        faithDatabase.postDatabaseDao.update()
    //  }
}