package com.example.ep3_devops_faith.database.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDatabaseDao {
    @Insert
    fun insert(databaseUser: DatabaseUser)
    @Update
    fun update(databaseUser: DatabaseUser)
    @Query("SELECT * FROM user_table WHERE auth0_key = :authO_key")
    fun getByKey(authO_key: String): DatabaseUser
    @Query("SELECT * FROM user_table ORDER BY Role")
    fun getAllUsersOrderedByRole(): LiveData<List<DatabaseUser>>
    @Query("SELECT * FROM user_table WHERE Role = :role ORDER BY Role")
    fun getAllUsersByRoleOrderedByRole(role: String): LiveData<List<DatabaseUser>>
}