package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FmStationDao {
    @Query("SELECT * FROM fm_stations ORDER BY frequencyMhz ASC")
    fun getAllStations(): Flow<List<FmStationEntity>>

    @Query("SELECT * FROM fm_stations WHERE isFavorite = 1 ORDER BY frequencyMhz ASC")
    fun getFavoriteStations(): Flow<List<FmStationEntity>>

    @Query("SELECT * FROM fm_stations WHERE frequencyMhz = :freq LIMIT 1")
    suspend fun getStationByFrequency(freq: Float): FmStationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: FmStationEntity): Long

    @Update
    suspend fun updateStation(station: FmStationEntity)

    @Delete
    suspend fun deleteStation(station: FmStationEntity)

    @Query("DELETE FROM fm_stations WHERE frequencyMhz = :freq")
    suspend fun deleteByFrequency(freq: Float)
}
