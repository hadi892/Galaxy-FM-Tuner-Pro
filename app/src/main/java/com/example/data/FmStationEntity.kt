package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fm_stations")
data class FmStationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val frequencyMhz: Float, // e.g. 98.5f
    val stationName: String, // e.g. "Galaxy Jazz FM"
    val rdsProgramService: String = "", // e.g. "GALAXY-J"
    val rdsProgramType: String = "POP M", // e.g. "ROCK M", "NEWS", "JAZZ"
    val isFavorite: Boolean = true,
    val signalStrengthDb: Int = 45, // simulated/probed signal strength
    val notes: String = ""
)
