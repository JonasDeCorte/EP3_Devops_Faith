package com.example.ep3_devops_faith.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ep3_devops_faith.network.MovieApi
import com.example.ep3_devops_faith.network.MoviePropertyList
import kotlinx.coroutines.launch

enum class MovieApiStatus { LOADING, ERROR, DONE }
/**
 * The [ViewModel] that is attached to the [MovieOverviewFragment].
 */
class MovieViewModel() : ViewModel() {
    // Internally, we use a MutableLiveData, because we will be updating the MoviePropertyList
    // with new values
    private val _properties = MutableLiveData<MoviePropertyList>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<MoviePropertyList>
        get() = _properties

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<MovieApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<MovieApiStatus>
        get() = _status

    /**
     * Call getAllMovies() on init so we can display status immediately.
     */
    init {
        getAllMovies()
    }

    /**
     * Gets all movies property information from the Movie API Retrofit service and updates the
     * [MoviePropertyList] [LiveData]. The Retrofit service returns a coroutine Deferred, which we
     * await to get the result of the transaction.
     */
    private fun getAllMovies() {
        viewModelScope.launch {
            _status.value = MovieApiStatus.LOADING
            try {
                _properties.value = MovieApi.retrofitService.getMovies()
                println(_properties.value)
                _status.value = MovieApiStatus.DONE
            } catch (e: Exception) {
                _status.value = MovieApiStatus.ERROR
            }
        }
    }
}