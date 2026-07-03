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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HeadsetOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.ui.theme.CyanDeep
import com.example.ui.theme.CyanElectric
import com.example.ui.theme.GreenSignal
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.RedAlert

@Composable
fun FmTopBar(
    isPoweredOn: Boolean,
    isHeadsetConnected: Boolean,
    isSpeakerOutput: Boolean,
    onPowerToggle: () -> Unit,
    onSpeakerToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanDeep)
                        .border(1.dp, CyanElectric, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = "FM Radio Icon",
                        tint = CyanElectric,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "GALAXY TAB A9+ FM TUNER",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "SM-X216B 5G | Offline Hardware Receiver",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Headphone Antenna Status Badge
                val antennaBg by animateColorAsState(if (isHeadsetConnected) GreenSignal.copy(alpha = 0.2f) else RedAlert.copy(alpha = 0.2f))
                val antennaBorder by animateColorAsState(if (isHeadsetConnected) GreenSignal else RedAlert)
                
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(antennaBg)
                        .border(1.dp, antennaBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isHeadsetConnected) Icons.Default.Headphones else Icons.Default.HeadsetOff,
                        contentDescription = "Antenna Status",
                        tint = antennaBorder,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHeadsetConnected) "ANTENNA READY" else "NEED HEADPHONES",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = antennaBorder
                    )
                }

                // Audio Output Route Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onSpeakerToggle() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSpeakerOutput) Icons.Default.Speaker else Icons.Default.Headphones,
                        contentDescription = "Audio Output Route",
                        tint = CyanElectric,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSpeakerOutput) "QUAD SPEAKERS" else "HEADSET",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Main Power Toggle Button
                val powerBg by animateColorAsState(if (isPoweredOn) CyanElectric else Color.DarkGray)
                val powerTint by animateColorAsState(if (isPoweredOn) Color.Black else Color.White)
                
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(powerBg)
                        .clickable { onPowerToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Power Toggle",
                        tint = powerTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
