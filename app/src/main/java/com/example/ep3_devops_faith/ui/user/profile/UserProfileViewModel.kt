package com.example.ep3_devops_faith.ui.user.profile

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserProfileViewModel(application: Application) :
    AndroidViewModel(application) {
    private val _saveEvent = MutableLiveData<Boolean>()
    val saveEvent: LiveData<Boolean>
        get() = _saveEvent

    init {
        _saveEvent.value = false
    }

    fun saveProfileClick() {
        _saveEvent.value = true
    }

    fun saveEventDone() {
        _saveEvent.value = false
    }

    fun saveUserProfile(name: String, avatar: Bitmap?, role: String?) {
        viewModelScope.launch {
        }
    }
}