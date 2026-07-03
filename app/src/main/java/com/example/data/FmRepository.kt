package com.example.data

import kotlinx.coroutines.flow.Flow

class FmRepository(private val dao: FmStationDao) {
    val allStations: Flow<List<FmStationEntity>> = dao.getAllStations()
    val favorites: Flow<List<FmStationEntity>> = dao.getFavoriteStations()

    suspend fun saveOrUpdatePreset(freq: Float, name: String, rdsPs: String = "", pty: String = "POP M") {
        val existing = dao.getStationByFrequency(freq)
        if (existing != null) {
            dao.updateStation(existing.copy(stationName = name, rdsProgramService = rdsPs, rdsProgramType = pty, isFavorite = true))
        } else {
            dao.insertStation(
                FmStationEntity(
                    frequencyMhz = freq,
                    stationName = name,
                    rdsProgramService = rdsPs.ifEmpty { "FM ${freq}" },
                    rdsProgramType = pty,
                    isFavorite = true,
                    signalStrengthDb = 52
                )
            )
        }
    }

    suspend fun removePreset(freq: Float) {
        dao.deleteByFrequency(freq)
    }

    suspend fun ensureDefaultPresets() {
        val list = listOf(
            FmStationEntity(frequencyMhz = 88.5f, stationName = "Galaxy Classic FM", rdsProgramService = "GLX-CLAS", rdsProgramType = "CLASSICS", signalStrengthDb = 54),
            FmStationEntity(frequencyMhz = 93.3f, stationName = "Tab A9+ Jazz & Soul", rdsProgramService = "TAB-JAZZ", rdsProgramType = "JAZZ", signalStrengthDb = 48),
            FmStationEntity(frequencyMhz = 98.1f, stationName = "Local News & Traffic", rdsProgramService = "NEWS-98", rdsProgramType = "NEWS", signalStrengthDb = 58),
            FmStationEntity(frequencyMhz = 101.9f, stationName = "Electro Synthwave", rdsProgramService = "SYNTH101", rdsProgramType = "ELECTRONIC", signalStrengthDb = 50),
            FmStationEntity(frequencyMhz = 104.5f, stationName = "Rock Broadcast 104", rdsProgramService = "ROCK-104", rdsProgramType = "ROCK M", signalStrengthDb = 46),
            FmStationEntity(frequencyMhz = 107.7f, stationName = "Community Campus Radio", rdsProgramService = "CAMPUS", rdsProgramType = "CULTURE", signalStrengthDb = 44)
        )
        for (item in list) {
            if (dao.getStationByFrequency(item.frequencyMhz) == null) {
                dao.insertStation(item)
            }
        }
    }
}
