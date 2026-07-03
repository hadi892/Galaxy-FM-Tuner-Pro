package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FmDatabase
import com.example.data.FmRepository
import com.example.data.FmStationEntity
import com.example.hardware.FmHardwareController
import com.example.hardware.HardwareProbeResult
import com.example.hardware.RdsData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = FmDatabase.getDatabase(application)
    private val repository = FmRepository(database.fmStationDao())
    val fmController = FmHardwareController(application)

    val currentFreq: StateFlow<Float> = fmController.currentFreq
    val isPoweredOn: StateFlow<Boolean> = fmController.isPoweredOn
    val isHeadsetAntennaConnected: StateFlow<Boolean> = fmController.isHeadsetAntennaConnected
    val isSpeakerOutput: StateFlow<Boolean> = fmController.isSpeakerOutput
    val isStereoMode: StateFlow<Boolean> = fmController.isStereoMode
    val probeResult: StateFlow<HardwareProbeResult> = fmController.probeResult
    val rdsData: StateFlow<RdsData> = fmController.rdsData

    val presetsList: StateFlow<List<FmStationEntity>> = repository.allStations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private val _sleepTimerRemainingMin = MutableStateFlow(0)
    val sleepTimerRemainingMin: StateFlow<Int> = _sleepTimerRemainingMin.asStateFlow()

    private var recordJob: Job? = null
    private var sleepJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureDefaultPresets()
        }
    }

    fun togglePower() {
        val newState = !isPoweredOn.value
        fmController.setPower(newState)
        if (!newState && _isRecording.value) {
            stopRecording()
        }
    }

    fun reProbeHardware() {
        fmController.runHardwareProbeAndBind()
    }

    fun setFrequency(freq: Float) {
        fmController.setFrequency(freq)
    }

    fun tuneStepUp() {
        fmController.tuneUp()
    }

    fun tuneStepDown() {
        fmController.tuneDown()
    }

    fun seekUp() {
        val currentKnown = presetsList.value.map { it.frequencyMhz }
        fmController.seekNextStation(currentKnown)
    }

    fun seekDown() {
        val currentKnown = presetsList.value.map { it.frequencyMhz }
        fmController.seekPrevStation(currentKnown)
    }

    fun toggleSpeakerRouting() {
        fmController.toggleSpeakerOutput()
    }

    fun toggleStereoMono() {
        fmController.toggleStereoMode()
    }

    fun saveCurrentStationAsPreset(customName: String, categoryTag: String) {
        viewModelScope.launch {
            val freq = currentFreq.value
            val rds = rdsData.value
            repository.saveOrUpdatePreset(
                freq = freq,
                name = customName.ifBlank { "Preset ${freq} MHz" },
                rdsPs = rds.programServicePs,
                pty = categoryTag.ifBlank { rds.programTypePty }
            )
        }
    }

    fun removePreset(freq: Float) {
        viewModelScope.launch {
            repository.removePreset(freq)
        }
    }

    fun toggleRecording() {
        if (_isRecording.value) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        if (!isPoweredOn.value) {
            fmController.setPower(true)
        }
        _isRecording.value = true
        _recordingSeconds.value = 0
        recordJob?.cancel()
        recordJob = viewModelScope.launch {
            while (_isRecording.value) {
                delay(1000)
                _recordingSeconds.value += 1
            }
        }
    }

    private fun stopRecording() {
        _isRecording.value = false
        recordJob?.cancel()
        recordJob = null
    }

    fun setSleepTimer(minutes: Int) {
        sleepJob?.cancel()
        _sleepTimerRemainingMin.value = minutes
        if (minutes > 0) {
            sleepJob = viewModelScope.launch {
                var remainingSec = minutes * 60
                while (remainingSec > 0 && isPoweredOn.value) {
                    delay(1000)
                    remainingSec -= 1
                    if (remainingSec % 60 == 0) {
                        _sleepTimerRemainingMin.value = remainingSec / 60
                    }
                }
                if (isPoweredOn.value) {
                    fmController.setPower(false)
                }
                _sleepTimerRemainingMin.value = 0
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fmController.release()
    }
}
