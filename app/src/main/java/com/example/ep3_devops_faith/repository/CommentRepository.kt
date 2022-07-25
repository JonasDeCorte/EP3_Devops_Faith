package com.example.ep3_devops_faith.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.comment.DatabaseComment
import com.example.ep3_devops_faith.database.comment.asDomainmodel
import com.example.ep3_devops_faith.domain.Comment
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
                UserEmail = comment.UserEmail,
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

    suspend fun update(comment: Comment) {
        withContext(Dispatchers.IO) {
            var databaseComment = faithDatabase.commentDatabaseDao.get(comment.Id)
            databaseComment.Message = comment.Message
            faithDatabase.commentDatabaseDao.update(databaseComment)
            Timber.i("update comment success")
        }
    }

    suspend fun delete(comment: Comment) {
        withContext(Dispatchers.IO) {
            val databaseComment = faithDatabase.commentDatabaseDao.get(comment.Id)
            faithDatabase.commentDatabaseDao.delete(databaseComment)
            Timber.i("delete comment success")
        }
    }
}