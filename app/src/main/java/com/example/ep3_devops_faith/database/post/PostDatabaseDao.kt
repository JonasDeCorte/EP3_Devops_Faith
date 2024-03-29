package com.example.ep3_devops_faith.database.post

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PostDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(databasePost: DatabasePost)

    @Delete
    fun delete(databasePost: DatabasePost)

    @Query(value = "SELECT * FROM post_table WHERE Id= :id")
    fun get(id: Long): DatabasePost

    @Query(value = "SELECT * FROM post_table ORDER BY Id DESC")
    fun getAllEntries(): LiveData<List<DatabasePost>>
    @Update
    fun update(post: DatabasePost)

    @Query("SELECT COUNT(*) FROM post_table")
    fun getDataCount(): Int
}