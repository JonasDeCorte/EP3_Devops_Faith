package com.example.ep3_devops_faith.repository

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
            Timber.i("post insert UserId ${post.UserId} ")
            val newDatabaseFavorite = DatabaseFavorite(
                PostId = post.Id,
                UserId = CredentialsManager.cachedUserProfile?.getId()!!
            )
            Timber.i("newDatabaseFavorite $newDatabaseFavorite")
            faithDatabase.favoriteDatabaseDao.insert(newDatabaseFavorite)
            Timber.i("insert FAV success")
        }
    }
    suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            faithDatabase.favoriteDatabaseDao.getDataCount()
        }
    }
    suspend fun countUserFav(): List<Long> {
        return withContext(Dispatchers.IO) {
            Timber.i("countUserFavUserId ===  ${CredentialsManager.cachedUserProfile?.getId()!!}")
            faithDatabase.favoriteDatabaseDao.getDataCountFavorites(CredentialsManager.cachedUserProfile?.getId()!!)
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