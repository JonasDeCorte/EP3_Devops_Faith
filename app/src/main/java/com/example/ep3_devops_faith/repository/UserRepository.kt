package com.example.ep3_devops_faith.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.user.DatabaseUser
import com.example.ep3_devops_faith.database.user.asDomainmodel
import com.example.ep3_devops_faith.domain.User
import timber.log.Timber

class UserRepository(private val faithDatabase: FaithDatabase) {
    suspend fun getAllUsersOrderedByRole(): LiveData<List<User>> {
        return Transformations.map(faithDatabase.userDatabaseDao.getAllUsersOrderedByRole()) {
            it.asDomainmodel()
        }
    }

    suspend fun getAllUsersByRoleOrderedByRole(role: String): LiveData<List<User>> {
        return Transformations.map(faithDatabase.userDatabaseDao.getAllUsersByRoleOrderedByRole(role)) {
            it.asDomainmodel()
        }
    }

    suspend fun getByKey(auth0_key: String): User {
        return faithDatabase.userDatabaseDao.getByKey(auth0_key).asDomainmodel()
    }

    suspend fun insert(user: User) {
        Timber.i("insert user called")
        val newUser = DatabaseUser(
            Name = user.Name,
            Auth0Key = user.Auth0_key,
            ProfileImageUrl = user.ProfileImageUrl,
            Role = user.Role
        )
        faithDatabase.userDatabaseDao.insert(newUser)
        Timber.i("insert user success")
    }
}
