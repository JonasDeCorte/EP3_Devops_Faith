package com.example.ep3_devops_faith.database.comment

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CommentDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(databaseComment: DatabaseComment)
    @Update
    fun update(databaseComment: DatabaseComment)
    @Delete
    fun delete(databaseComment: DatabaseComment)

    @Query(value = "SELECT * FROM comment_table WHERE Id= :id")
    fun get(id: Long): DatabaseComment

    @Query(value = "SELECT * FROM comment_table WHERE post_id = :post_id ORDER BY Id DESC")
    fun getAllCommentForPostWithId(post_id: Long): LiveData<List<DatabaseComment>>

    @Query("SELECT COUNT(*) FROM comment_table")
    fun getDataCount(): Int
}