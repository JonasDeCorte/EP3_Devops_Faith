package com.example.ep3_devops_faith.ui.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ep3_devops_faith.database.user.DatabaseUser
import com.example.ep3_devops_faith.database.user.UserDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class UserProfileViewModel(
    val database: UserDatabaseDao,
    application: Application
) : AndroidViewModel(application) {
    private val _saveEvent = MutableLiveData<Boolean>()
    val saveEvent: LiveData<Boolean>
        get() = _saveEvent

    // TODO
    // private val db = FaithDatabase.getInstance(application.applicationContext)
    // private val repository = FaithRepository(db)
    init {
        _saveEvent.value = false
    }

    fun saveProfileClick() {
        _saveEvent.value = true
    }

    fun saveEventDone() {
        _saveEvent.value = false
    }

    // TODO make sure this works with a repository
    fun saveProfile(name: String, role: String) {
        viewModelScope.launch {
            val user = DatabaseUser()
            user.Name = name
            user.Role = role
            updateUserToDatabase(user)
        }
    }
    // TODO change to work with repo + update instead of insert
    private suspend fun updateUserToDatabase(user: DatabaseUser) {
        withContext(Dispatchers.IO) {
            database.insert(user)
        }
    }
}
