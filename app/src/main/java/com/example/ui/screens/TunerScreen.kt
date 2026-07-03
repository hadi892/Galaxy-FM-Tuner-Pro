package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.FmStationEntity
import com.example.hardware.RdsData
import com.example.ui.components.AddPresetDialog
import com.example.ui.components.FrequencyDisplay
import com.example.ui.components.SignalMeter
import com.example.ui.components.TunerControls

@Composable
fun TunerScreen(
    frequency: Float,
    isPoweredOn: Boolean,
    rdsData: RdsData,
    isStereoMode: Boolean,
    isRecording: Boolean,
    recordingSeconds: Int,
    sleepTimerMinutes: Int,
    presets: List<FmStationEntity>,
    onFrequencyChanged: (Float) -> Unit,
    onStepDown: () -> Unit,
    onStepUp: () -> Unit,
    onSeekDown: () -> Unit,
    onSeekUp: () -> Unit,
    onToggleRecord: () -> Unit,
    onSleepTimerSelect: () -> Unit,
    onSavePreset: (String, String) -> Unit
) {
    var showPresetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FrequencyDisplay(
            frequency = frequency,
            isPoweredOn = isPoweredOn,
            rdsData = rdsData,
            isStereoMode = isStereoMode
        )

        SignalMeter(
            isPoweredOn = isPoweredOn,
            rdsData = rdsData
        )

        TunerControls(
            frequency = frequency,
            isPoweredOn = isPoweredOn,
            isRecording = isRecording,
            recordingSeconds = recordingSeconds,
            sleepTimerMinutes = sleepTimerMinutes,
            presets = presets,
            onFrequencyChanged = onFrequencyChanged,
            onStepDown = onStepDown,
            onStepUp = onStepUp,
            onSeekDown = onSeekDown,
            onSeekUp = onSeekUp,
            onToggleRecord = onToggleRecord,
            onSleepTimerSelect = onSleepTimerSelect,
            onAddPresetClick = { showPresetDialog = true }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showPresetDialog) {
        val existing = presets.find { kotlin.math.abs(it.frequencyMhz - frequency) < 0.05f }
        AddPresetDialog(
            frequency = frequency,
            initialName = existing?.stationName ?: rdsData.programServicePs,
            initialTag = existing?.rdsProgramType ?: rdsData.programTypePty,
            onDismiss = { showPresetDialog = false },
            onSave = { name, tag ->
                onSavePreset(name, tag)
                showPresetDialog = false
            }
        )
    }
}
