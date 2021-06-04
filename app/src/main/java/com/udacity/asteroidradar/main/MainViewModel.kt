package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.repository.FilterType
import kotlinx.coroutines.launch
import java.util.*


class MainViewModel(application: Application) : ViewModel() {

    private val database = getDatabase(application)
    private val asteroidesRepository = AsteroidRepository(database)

    val asteroides = asteroidesRepository.asteroides

    val imageofTheDay = asteroidesRepository.pictureOfTheDayData

    init {
        if (isNetworkAvailable(application)) {
            viewModelScope.launch {
                asteroidesRepository.getNasaAsteroids()
                asteroidesRepository.getPictureOfDay()
            }
        }
    }

    private fun isNetworkAvailable(application: Application): Boolean {
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun applyFilter(filter : FilterType){
        asteroidesRepository.applyFilter(filter)
    }


    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}