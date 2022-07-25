package com.example.ep3_devops_faith.repository

import androidx.lifecycle.LiveData
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.favorite.DatabaseFavorite
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.login.CredentialsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class FavoriteRepository(private val faithDatabase: FaithDatabase) {
    // Database call
    suspend fun insert(post: Post) {
        // switch context to IO thread
        withContext(Dispatchers.IO) {
            Timber.i("insert FAV called")
            val newDatabaseFavorite = DatabaseFavorite(
                PostId = post.Id,
                UserId = post.UserId
            )
            faithDatabase.favoriteDatabaseDao.insert(newDatabaseFavorite)
            Timber.i("insert FAV success")
        }
    }

    fun getUserFavorites(userName: String): LiveData<List<Long>> {
        return faithDatabase.favoriteDatabaseDao.getUserFavorites(userName)
    }

    suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            faithDatabase.favoriteDatabaseDao.getDataCount()
        }
    }

    suspend fun get(postId: Long): DatabaseFavorite {
        return withContext(Dispatchers.IO) {
            faithDatabase.favoriteDatabaseDao.get(postId,
                CredentialsManager.cachedUserProfile?.getId()!!)
        }
    }

    suspend fun delete(post: Post) {
        return withContext(Dispatchers.IO) {
            val databaseFavorite = get(post.Id)
            Timber.i("delete.get = $databaseFavorite")
            if (databaseFavorite != null) {
                faithDatabase.favoriteDatabaseDao.delete(databaseFavorite)
                Timber.i("deleted")
            }
        }
    }
}