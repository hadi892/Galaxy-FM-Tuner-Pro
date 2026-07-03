package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.hardware.RdsData
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.CyanElectric
import com.example.ui.theme.GreenSignal
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.RedAlert
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun SignalMeter(
    isPoweredOn: Boolean,
    rdsData: RdsData
) {
    val signalNorm by animateFloatAsState(
        targetValue = if (isPoweredOn) (rdsData.signalStrengthDb / 65f).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "signal_meter"
    )

    val snrNorm by animateFloatAsState(
        targetValue = if (isPoweredOn) (rdsData.snrDb / 50f).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "snr_meter"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VHF RF SIGNAL ANALYSIS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MutedSlate
                )
                Text(
                    text = if (isPoweredOn) "${rdsData.signalStrengthDb} dBµV | SNR: ${rdsData.snrDb} dB" else "OFFLINE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = if (rdsData.signalStrengthDb > 35 && isPoweredOn) GreenSignal else if (isPoweredOn) AmberNeon else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // RF Level Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "RSSI",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.width(44.dp),
                    color = Color.White
                )
                Box(modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp))) {
                    LinearProgressIndicator(
                        progress = { signalNorm },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color = if (rdsData.signalStrengthDb > 40) GreenSignal else if (rdsData.signalStrengthDb > 25) AmberNeon else RedAlert,
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SNR Quality Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SNR",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.width(44.dp),
                    color = Color.White
                )
                Box(modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp))) {
                    LinearProgressIndicator(
                        progress = { snrNorm },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color = CyanElectric,
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Equalizer / Spectrum Bar Visualizer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val numBars = 32
                for (i in 0 until numBars) {
                    val targetBarHeight = if (isPoweredOn && rdsData.signalStrengthDb > 20) {
                        val wave = abs(sin((i * 0.45) + (rdsData.signalStrengthDb * 0.1))).toFloat()
                        (wave * (rdsData.signalStrengthDb / 65f)).coerceIn(0.1f, 1f)
                    } else {
                        0.05f
                    }

                    val barHeight by animateFloatAsState(targetValue = targetBarHeight, label = "bar_$i")

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(barHeight)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(if (i % 2 == 0) CyanElectric else AmberNeon)
                    )
                }
            }
        }
    }
}
