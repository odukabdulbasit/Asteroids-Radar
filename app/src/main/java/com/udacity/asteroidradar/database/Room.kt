package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AsteroidDao {
    @Query("SELECT * from table_asteroides ORDER by closeApproachDate ASC")
    fun getListAsteroid(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * from table_picture_of_the_day WHERE id=1")
    fun getPictureOfTheDay(): LiveData<DatabasePicture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAsteroids(asteroids: List<DatabaseAsteroid>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfTheDay(picture: DatabasePicture)

    @Query("DELETE FROM table_asteroides")
    fun deleteAllAsteroids()

    @Query("DELETE FROM table_picture_of_the_day")
    fun deleteAllPictures()

    @Query("select * from table_asteroides where closeApproachDate = :today")
    fun getAsteroidsFromToday(today: String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from table_asteroides where closeApproachDate between :startDate and :endDate")
    fun getAsteroidsFromWeek(startDate: String, endDate: String): LiveData<List<DatabaseAsteroid>>
}

@Database(entities = [DatabaseAsteroid::class, DatabasePicture::class], version = 2)
abstract class AsteroidDatabase: RoomDatabase() {
    abstract val asteroid: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext, AsteroidDatabase::class.java, "asteroid")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

    return INSTANCE
}

