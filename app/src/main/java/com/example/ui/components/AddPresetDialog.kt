package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CyanElectric
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.SlateGlass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPresetDialog(
    frequency: Float,
    initialName: String,
    initialTag: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var stationName by remember { mutableStateOf(initialName) }
    var tag by remember { mutableStateOf(initialTag) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CarbonSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanElectric, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "SAVE PRESET STATION",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = CyanElectric
                )
                Text(
                    text = "Frequency: ${frequency} MHz",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    color = AmberNeon
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = stationName,
                    onValueChange = { stationName = it },
                    label = { Text("Station Name / Call Sign") },
                    placeholder = { Text("e.g. Galaxy Jazz FM") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanElectric,
                        unfocusedBorderColor = SlateGlass,
                        focusedLabelColor = CyanElectric
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Program Type (PTY / Genre)") },
                    placeholder = { Text("e.g. ROCK, NEWS, JAZZ, CLASSIC") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanElectric,
                        unfocusedBorderColor = SlateGlass,
                        focusedLabelColor = CyanElectric
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = MutedSlate)
                    }
                    Button(
                        onClick = { onSave(stationName, tag) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanElectric)
                    ) {
                        Text("SAVE PRESET", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
