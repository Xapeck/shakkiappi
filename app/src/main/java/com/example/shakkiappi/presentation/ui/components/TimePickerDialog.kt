package com.example.shakkiappi.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shakkiappi.data.model.TimeControl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeControlDialog(
    onDismiss: () -> Unit,
    onTimeControlSelected: (TimeControl) -> Unit
) {
    var customMinutes by remember { mutableStateOf("5") }
    var customSeconds by remember { mutableStateOf("0") }
    var incrementSeconds by remember { mutableStateOf("0") }
    var timeControlType by remember { mutableStateOf("FISCHER") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Valitse aikakontrolli") },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                Text("Esiasetukset", style = MaterialTheme.typography.titleSmall)
                LazyRow {
                    items(TimeControl.PRESETS) { preset ->
                        FilterChip(
                            selected = false,
                            onClick = { onTimeControlSelected(preset.second) },
                            label = { Text(preset.first) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Custom-aika", style = MaterialTheme.typography.titleSmall)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(customMinutes, { customMinutes = it }, label = { Text("min") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(customSeconds, { customSeconds = it }, label = { Text("s") }, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
                Text("Aikakontrollin tyyppi", style = MaterialTheme.typography.titleSmall)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(timeControlType == "FISCHER", { timeControlType = "FISCHER" }, label = { Text("Fischer") })
                    FilterChip(timeControlType == "BRONSTEIN", { timeControlType = "BRONSTEIN" }, label = { Text("Bronstein") })
                    FilterChip(timeControlType == "SIMPLE", { timeControlType = "SIMPLE" }, label = { Text("Yksinkertainen") })
                }
                if (timeControlType != "SIMPLE") {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(incrementSeconds, { incrementSeconds = it }, label = { Text(if (timeControlType == "FISCHER") "Lisäys (s)" else "Viive (s)") }, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            TextButton({
                val total = (customMinutes.toIntOrNull() ?: 0) * 60 + (customSeconds.toIntOrNull() ?: 0)
                val inc = (incrementSeconds.toLongOrNull() ?: 0) * 1000
                val tc = when (timeControlType) {
                    "FISCHER" -> TimeControl.Fischer(total * 1000L, inc)
                    "BRONSTEIN" -> TimeControl.Bronstein(total * 1000L, inc)
                    else -> TimeControl.Simple(total * 1000L)
                }
                onTimeControlSelected(tc)
                onDismiss()
            }) { Text("Aloita peli") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Peruuta") } }
    )
}
