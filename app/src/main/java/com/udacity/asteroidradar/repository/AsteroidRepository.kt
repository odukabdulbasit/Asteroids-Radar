package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.apiKey
import com.udacity.asteroidradar.Constants.startDate
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.DateHelper
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

enum class FilterType { WEEKLY, TODAY, SAVED }

class AsteroidRepository (private val database: AsteroidDatabase) {

    private val filterType = MutableLiveData(FilterType.WEEKLY)

    val asteroides: LiveData<List<Asteroid>> = Transformations.switchMap(filterType) { filter ->
        when (filter) {
            FilterType.TODAY ->
                Transformations.map(database.asteroid.getAsteroidsFromToday(DateHelper.getToday())) { it.asDomainModel() }
            FilterType.WEEKLY ->
                Transformations.map(
                    database.asteroid.getAsteroidsFromWeek(
                        DateHelper.getToday(),
                        DateHelper.getOneWeekAhead()
                    )
                ) { it.asDomainModel() }
            FilterType.SAVED ->
                Transformations.map(database.asteroid.getListAsteroid()) { it.asDomainModel() }
        }
    }

    fun applyFilter(filter: FilterType) {
        filterType.value = filter
    }

    val pictureOfTheDayData: LiveData<PictureOfDay> = Transformations.map(database.asteroid.getPictureOfTheDay()) {
        it?.let {
            it.asDomainModel()
        }
    }

    suspend fun getNasaAsteroids(){
        withContext(Dispatchers.IO){
                Constants.calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
                val endDate = Constants.dateFormat.format(Constants.calendar.time)

                val asteroidList = AsteroidApi.retrofitService.getAsteroidListString(startDate, endDate, apiKey)
                database.asteroid.insertAllAsteroids(parseAsteroidsJsonResult(JSONObject(asteroidList)))
        }
    }

   suspend fun getPictureOfDay(){
       withContext(Dispatchers.IO){
                val pictureOfTheDay = AsteroidApi.retrofitService.getImageOfTheDay(apiKey)
                database.asteroid.insertPictureOfTheDay(pictureOfTheDay.asDatabaseModel())
        }
    }
}
