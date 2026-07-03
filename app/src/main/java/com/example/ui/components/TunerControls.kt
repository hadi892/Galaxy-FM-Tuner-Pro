package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FmStationEntity
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.CyanDeep
import com.example.ui.theme.CyanElectric
import com.example.ui.theme.MagentaLaser
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.RedAlert
import com.example.ui.theme.SlateGlass
import kotlin.math.abs

@Composable
fun TunerControls(
    frequency: Float,
    isPoweredOn: Boolean,
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
    onAddPresetClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        // Frequency Slider Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("87.5 MHz", style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace), color = MutedSlate)
            Text("VHF TUNING DIAL", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = CyanElectric)
            Text("108.0 MHz", style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace), color = MutedSlate)
        }

        Slider(
            value = frequency,
            onValueChange = { onFrequencyChanged(it) },
            valueRange = 87.5f..108.0f,
            enabled = isPoweredOn,
            colors = SliderDefaults.colors(
                thumbColor = CyanElectric,
                activeTrackColor = CyanElectric,
                inactiveTrackColor = SlateGlass
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tuning Control Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Auto Seek Down
            ControlCircleButton(
                icon = Icons.Default.SkipPrevious,
                label = "SEEK -",
                enabled = isPoweredOn,
                onClick = onSeekDown
            )

            // Fine Step Down (-0.1)
            ControlTextButton(
                text = "-0.1",
                enabled = isPoweredOn,
                onClick = onStepDown
            )

            // Center Dial Badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isPoweredOn) CyanDeep else SlateGlass)
                    .border(2.dp, if (isPoweredOn) CyanElectric else Color.DarkGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Tuning",
                    tint = if (isPoweredOn) CyanElectric else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Fine Step Up (+0.1)
            ControlTextButton(
                text = "+0.1",
                enabled = isPoweredOn,
                onClick = onStepUp
            )

            // Auto Seek Up
            ControlCircleButton(
                icon = Icons.Default.SkipNext,
                label = "SEEK +",
                enabled = isPoweredOn,
                onClick = onSeekUp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Toolbar: Record, Sleep Timer, Save Preset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Record Live FM Button
            val recBg by animateColorAsState(if (isRecording) RedAlert.copy(alpha = 0.2f) else SlateGlass)
            val recBorder by animateColorAsState(if (isRecording) RedAlert else Color.Transparent)
            
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(recBg)
                    .border(1.dp, recBorder, RoundedCornerShape(12.dp))
                    .clickable(enabled = isPoweredOn) { onToggleRecord() }
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FiberManualRecord,
                        contentDescription = "Record",
                        tint = if (isRecording) RedAlert else Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRecording) String.format("REC %02d:%02d", recordingSeconds / 60, recordingSeconds % 60) else "RECORD",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (isRecording) RedAlert else Color.White
                    )
                }
            }

            // Sleep Timer Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (sleepTimerMinutes > 0) AmberNeon.copy(alpha = 0.2f) else SlateGlass)
                    .border(1.dp, if (sleepTimerMinutes > 0) AmberNeon else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable(enabled = isPoweredOn) { onSleepTimerSelect() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Sleep Timer",
                        tint = if (sleepTimerMinutes > 0) AmberNeon else Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (sleepTimerMinutes > 0) "${sleepTimerMinutes}m SLEEP" else "TIMER",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (sleepTimerMinutes > 0) AmberNeon else Color.White
                    )
                }
            }

            // Save Preset Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyanElectric.copy(alpha = 0.15f))
                    .border(1.dp, CyanElectric, RoundedCornerShape(12.dp))
                    .clickable(enabled = isPoweredOn) { onAddPresetClick() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Save Preset",
                        tint = CyanElectric,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PRESET",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = CyanElectric
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Preset Stations Horizontal Carousel
        Text(
            text = "SAVED PRESET STATIONS (${presets.size})",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            color = MutedSlate
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(presets) { preset ->
                val isSelected = abs(frequency - preset.frequencyMhz) < 0.05f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) CyanDeep else Color(0xFF1E293B))
                        .border(1.dp, if (isSelected) CyanElectric else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable(enabled = isPoweredOn) { onFrequencyChanged(preset.frequencyMhz) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Column {
                        Text(
                            text = "${preset.frequencyMhz} MHz",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (isSelected) CyanElectric else Color.White
                        )
                        Text(
                            text = preset.stationName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) AmberNeon else MutedSlate
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ControlCircleButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, enabled: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (enabled) SlateGlass else Color(0xFF111827))
                .clickable(enabled = enabled) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = if (enabled) CyanElectric else Color.DarkGray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MutedSlate)
    }
}

@Composable
fun ControlTextButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) SlateGlass else Color(0xFF111827))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
            color = if (enabled) AmberNeon else Color.DarkGray
        )
    }
}
