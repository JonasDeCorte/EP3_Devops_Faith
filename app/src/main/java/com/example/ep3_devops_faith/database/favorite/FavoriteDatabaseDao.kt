package com.example.ep3_devops_faith.database.favorite

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg databaseFavorite: DatabaseFavorite)

    @Delete
    fun delete(databaseFavorite: DatabaseFavorite)

    @Query(value = "SELECT * FROM favorite_table WHERE post_id = :post_id AND user_id = :user_id")
    fun get(post_id: Long, user_id: String): DatabaseFavorite

    @Query(value = "SELECT post_id FROM favorite_table WHERE user_id = :user_id")
    fun getUserFavorites(user_id: String): LiveData<List<Long>>

    @Query("SELECT COUNT(*) FROM favorite_table")
    fun getDataCount(): Int

    @Query("SELECT post_id FROM favorite_table WHERE user_id = :user_id")
    fun getDataCountFavorites(user_id: String): List<Long>
}