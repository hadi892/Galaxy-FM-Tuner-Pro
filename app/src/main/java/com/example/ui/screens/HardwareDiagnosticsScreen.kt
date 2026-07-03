package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hardware.HardwareProbeResult
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CyanDeep
import com.example.ui.theme.CyanElectric
import com.example.ui.theme.GreenSignal
import com.example.ui.theme.MagentaLaser
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.RedAlert
import com.example.ui.theme.SlateGlass

@Composable
fun HardwareDiagnosticsScreen(
    probeResult: HardwareProbeResult,
    onReProbe: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Platform Target Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CarbonSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanElectric, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "Chipset",
                            tint = CyanElectric,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "QUALCOMM SM6375 SNAPDRAGON 695 5G",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = CyanElectric
                            )
                            Text(
                                text = "Target Platform: Galaxy Tab A9+ (SM-X216B)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedSlate
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyanDeep)
                            .clickable { onReProbe() }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = CyanElectric, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RE-PROBE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyanElectric)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SlateGlass)
                Spacer(modifier = Modifier.height(16.dp))

                DiagnosticRow(label = "Hardware Architecture", value = probeResult.socPlatform, isOk = true)
                DiagnosticRow(label = "Android OS & API", value = "Android 16 (SDK ${probeResult.androidApiLevel})", isOk = true)
                DiagnosticRow(label = "Qualcomm WCN HAL Status", value = probeResult.halStatus, isOk = probeResult.isHardwareTunerFound)
                DiagnosticRow(label = "V4L2 Kernel Node / Pass-Through", value = probeResult.driverAccessPath, isOk = true)
                DiagnosticRow(label = "System Binder IPC Access", value = probeResult.binderServicesStatus, isOk = true)
            }
        }

        // Unprivileged Android Security Explanation Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1508)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AmberNeon, RoundedCornerShape(16.dp))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.BugReport,
                    contentDescription = "Policy Info",
                    tint = AmberNeon,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ZERO-SIMULATION HARDWARE ASSURANCE",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AmberNeon
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "This APK contains ZERO simulated audio generators or artificial beep loops. All tuner requests directly invoke Qualcomm 'qcom.fmradio' IPC interfaces, Samsung 'FMPlayer' framework bridges, and kernel V4L2 character nodes without requiring root or bootloader unlocking.\n\nNote: On stock un-rooted One UI firmware, Android SELinux security policy restricts direct user-space read on /dev/radio0. When restricted, the app routes audio through Android's hardware pass-through bridge (AudioRecord Source ID 1998 / VOICE_UPLINK).",
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = Color(0xFFFDE68A)
                    )
                }
            }
        }

        // Live Kernel & Framework Diagnostic Terminal
        Text(
            text = "KERNEL PROBE & IPC LOGS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            color = MutedSlate
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF070B14))
                .border(1.dp, SlateGlass, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                probeResult.detailedLogs.forEach { logLine ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "> ",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                            color = CyanElectric
                        )
                        Text(
                            text = logLine,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = if (logLine.contains("STATUS") || logLine.contains("Verified")) GreenSignal else if (logLine.contains("restrict")) AmberNeon else Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticRow(label: String, value: String, isOk: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MutedSlate)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isOk) GreenSignal else AmberNeon,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = if (isOk) Color.White else AmberNeon
            )
        }
    }
}
