package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.FmTopBar
import com.example.ui.screens.HardwareDiagnosticsScreen
import com.example.ui.screens.TunerScreen
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CyanElectric
import com.example.ui.theme.GalacticBackground
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateGlass

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentFreq by viewModel.currentFreq.collectAsStateWithLifecycle()
    val isPoweredOn by viewModel.isPoweredOn.collectAsStateWithLifecycle()
    val isHeadsetAntennaConnected by viewModel.isHeadsetAntennaConnected.collectAsStateWithLifecycle()
    val isSpeakerOutput by viewModel.isSpeakerOutput.collectAsStateWithLifecycle()
    val isStereoMode by viewModel.isStereoMode.collectAsStateWithLifecycle()
    val probeResult by viewModel.probeResult.collectAsStateWithLifecycle()
    val rdsData by viewModel.rdsData.collectAsStateWithLifecycle()
    val presets by viewModel.presetsList.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordingSeconds by viewModel.recordingSeconds.collectAsStateWithLifecycle()
    val sleepTimerRemainingMin by viewModel.sleepTimerRemainingMin.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0: Tuner, 1: SM6375 Diagnostics
    var showSleepModal by remember { mutableStateOf(false) }
    var selectedSleepVal by remember { mutableFloatStateOf(15f) }

    // Request necessary runtime permissions for hardware audio routing pass-through
    val requiredPermissions = remember {
        val list = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        list.toTypedArray()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // After permission check, re-probe hardware in case audio bridge was unlocked
        viewModel.reProbeHardware()
    }

    LaunchedEffect(Unit) {
        val missing = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GalacticBackground,
        topBar = {
            FmTopBar(
                isPoweredOn = isPoweredOn,
                isHeadsetConnected = isHeadsetAntennaConnected,
                isSpeakerOutput = isSpeakerOutput,
                onPowerToggle = { viewModel.togglePower() },
                onSpeakerToggle = { viewModel.toggleSpeakerRouting() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CarbonSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Radio,
                            contentDescription = "Tuner Dial"
                        )
                    },
                    label = { Text("FM TUNER", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanElectric,
                        indicatorColor = CyanElectric,
                        unselectedIconColor = MutedSlate,
                        unselectedTextColor = MutedSlate
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "SM6375 Hardware"
                        )
                    },
                    label = { Text("SM6375 DIAGNOSTICS", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanElectric,
                        indicatorColor = CyanElectric,
                        unselectedIconColor = MutedSlate,
                        unselectedTextColor = MutedSlate
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedTab == 0) {
                TunerScreen(
                    frequency = currentFreq,
                    isPoweredOn = isPoweredOn,
                    rdsData = rdsData,
                    isStereoMode = isStereoMode,
                    isRecording = isRecording,
                    recordingSeconds = recordingSeconds,
                    sleepTimerMinutes = sleepTimerRemainingMin,
                    presets = presets,
                    onFrequencyChanged = { viewModel.setFrequency(it) },
                    onStepDown = { viewModel.tuneStepDown() },
                    onStepUp = { viewModel.tuneStepUp() },
                    onSeekDown = { viewModel.seekDown() },
                    onSeekUp = { viewModel.seekUp() },
                    onToggleRecord = { viewModel.toggleRecording() },
                    onSleepTimerSelect = { showSleepModal = true },
                    onSavePreset = { name, tag -> viewModel.saveCurrentStationAsPreset(name, tag) }
                )
            } else {
                HardwareDiagnosticsScreen(
                    probeResult = probeResult,
                    onReProbe = { viewModel.reProbeHardware() }
                )
            }
        }
    }

    if (showSleepModal) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showSleepModal = false },
            sheetState = sheetState,
            containerColor = CarbonSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AUTO POWER-OFF SLEEP TIMER",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = CyanElectric
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${selectedSleepVal.toInt()} Minutes",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = AmberNeon
                )
                Spacer(modifier = Modifier.height(16.dp))
                Slider(
                    value = selectedSleepVal,
                    onValueChange = { selectedSleepVal = it },
                    valueRange = 0f..120f,
                    steps = 11,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanElectric,
                        activeTrackColor = CyanElectric,
                        inactiveTrackColor = SlateGlass
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SlateGlass)
                            .clickable {
                                viewModel.setSleepTimer(0)
                                showSleepModal = false
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("DISABLE TIMER", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyanElectric)
                            .clickable {
                                viewModel.setSleepTimer(selectedSleepVal.toInt())
                                showSleepModal = false
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SET TIMER", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
