package com.example.ep3_devops_faith.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.comment.DatabaseComment
import com.example.ep3_devops_faith.database.comment.asDomainmodel
import com.example.ep3_devops_faith.domain.Comment
import com.example.ep3_devops_faith.domain.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class CommentRepository(private val faithDatabase: FaithDatabase) {

    suspend fun allCommentForPost(postId: Long): LiveData<List<Comment>> {
        return Transformations.map(faithDatabase.commentDatabaseDao.getAllCommentForPostWithId(
            postId)) {
            it.asDomainmodel()
        }
    }

    suspend fun insert(comment: Comment) {
        withContext(Dispatchers.IO) {
            Timber.i("insert comment called")
            val newDatabaseComment = DatabaseComment(
                Message = comment.Message,
                UserId = comment.UserId,
                PostId = comment.PostId
            )
            faithDatabase.commentDatabaseDao.insert(newDatabaseComment)
            Timber.i("insert comment success")
        }
    }

    suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            faithDatabase.commentDatabaseDao.getDataCount()
        }
    }

    suspend fun delete(post: Post) {
        withContext(Dispatchers.IO) {
            val databasePost = faithDatabase.commentDatabaseDao.get(post.Id)
            faithDatabase.commentDatabaseDao.delete(databasePost)
        }
    }
}